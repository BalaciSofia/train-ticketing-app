# Train Ticketing Application

A Spring Boot REST application for managing train schedules, bookings, and delay notifications. Supports two roles: **CLIENT** (search routes, book tickets) and **ADMIN** (manage the full catalogue, report delays).

---

## Technology Stack

- Java 25 / Spring Boot 4.0.6
- Spring Data JPA (Hibernate) with PostgreSQL
- Spring Security Crypto — BCrypt password hashing
- Spring Boot Mail — SMTP email via JavaMailSender
- springdoc-openapi — Swagger UI auto-generated at `/swagger-ui.html`
- JUnit 5 + Mockito — unit tests

---

## Database Schema

The full schema, seed data, and sequence resets are in [`plan/seed.sql`](plan/seed.sql). Running that script against a fresh PostgreSQL database gives a working dataset for development and testing.

The domain consists of ten tables:

- **stations** — physical stops; each has a city name
- **routes** — named sequences of stations (e.g. Bucuresti – Brasov)
- **route_stops** — junction between a route and a station, with a `stop_number` defining the travel order
- **trains** — physical assets with a seat capacity and a service number (e.g. IR 1581)
- **schedules** — bind a train to a route for a specific run
- **schedule_stops** — the timetable entry for one stop within a schedule, storing both arrival and departure timestamps independently to model dwell time
- **users** — application accounts; passwords stored exclusively as BCrypt hashes
- **bookings** — groups one or more tickets under one transaction for a single user
- **tickets** — one seat on one schedule between two `schedule_stops` (departure and arrival)
- **delays** — a reported delay on a schedule starting from a given stop, used to drive email notifications

The central relationship is `schedule_stop`: it sits at the intersection of a `schedule` (which train, which route) and a `route_stop` (which station, in what position). A ticket references two `schedule_stops` on the same schedule — its board and alight points.

---

## Functionalities

### Authentication

**Login** — `POST /api/auth/login`

```json
{ "username": "sofia.balaci", "password": "password123" }
```

Returns `200` with `{ "id", "username", "role" }` on success, `401` on bad credentials.

**Register** — `POST /api/auth/register`

```json
{ "username": "ion.popescu", "email": "ion@example.com", "password": "mypassword" }
```

Returns `201` on success. Returns `409` if the username or email is already taken. New accounts are always created with the `CLIENT` role; admin accounts must be inserted directly in the database.

---

### Route Search

`GET /api/search?fromStationId={id}&toStationId={id}`

Returns all connections between the two stations across every schedule, split into direct routes and routes with one changeover. No date filtering is applied at the API level — every scheduled run that matches is returned, and callers can filter by date on their end.

**Example response:**

```json
{
  "directRoutes": [
    {
      "trainNumber": "IR 1581",
      "fromCity": "Bucuresti Nord",
      "departureTime": "2026-05-15T08:00:00",
      "toCity": "Brasov",
      "arrivalTime": "2026-05-15T11:30:00",
      "departureScheduleStopId": 1,
      "arrivalScheduleStopId": 4
    }
  ],
  "changeoverRoutes": [
    {
      "changeoverCity": "Brasov",
      "firstLeg": {
        "trainNumber": "IR 1581",
        "fromCity": "Bucuresti Nord",
        "departureTime": "2026-05-15T08:00:00",
        "toCity": "Brasov",
        "arrivalTime": "2026-05-15T11:30:00",
        "departureScheduleStopId": 1,
        "arrivalScheduleStopId": 4
      },
      "secondLeg": {
        "trainNumber": "IC 521",
        "fromCity": "Brasov",
        "departureTime": "2026-05-15T12:00:00",
        "toCity": "Cluj-Napoca",
        "arrivalTime": "2026-05-15T17:00:00",
        "departureScheduleStopId": 9,
        "arrivalScheduleStopId": 11
      }
    }
  ]
}
```

If no connection exists the response contains empty arrays — never an error status.

**Algorithm — two passes:**

1. **Direct routes.** Load all `schedule_stops` at the departure station and all at the arrival station. Group both sets by `schedule_id`. For every schedule present in both groups, check that the departure `stop_number` is lower than the arrival `stop_number` (ensuring the correct direction of travel). Each match becomes a `DirectRoute`.

2. **Changeover routes.** For each departure-station stop that did not produce a direct route, fetch all later stops on the same schedule (`findLaterStopsOnSchedule`). Each later stop is a candidate changeover station. For each candidate, find all `schedule_stops` at that station belonging to a *different* schedule. If any of those stops belongs to a schedule that also serves the final destination (with correct stop ordering), and the connecting departure is strictly after the changeover arrival, the two legs form a `ChangeoverRoute`.

**Possible improvement — multiple changeovers**

The current implementation supports at most one changeover. To support N changeovers the natural extension is a graph search:

- Model the network as a directed graph where nodes are `(station, schedule_stop)` pairs and edges represent either boarding a train (same schedule, increasing stop number, departure after arrival) or transferring between trains (same station, different schedule, sufficient connection time).
- Run a BFS or Dijkstra expansion from all `schedule_stops` at the departure station. Depth 0 gives direct routes, depth 1 gives one changeover, and so on. A maximum-depth cutoff (e.g. 3 changeovers) and a maximum total-journey-time cutoff keep the result set manageable.
- A priority queue keyed on estimated arrival time at the destination (Dijkstra-style) naturally surfaces the fastest journeys first.

This approach requires no schema changes — the existing `schedule_stops` table already contains all the information needed. Caching all `schedule_stops` in memory at startup as maps keyed by station and by schedule would make each BFS expansion nearly free in terms of database queries.

---

### Booking

`POST /api/bookings`

```json
{
  "userId": 2,
  "tickets": [
    { "departureScheduleStopId": 1, "arrivalScheduleStopId": 4 },
    { "departureScheduleStopId": 9, "arrivalScheduleStopId": 11 }
  ]
}
```

The `tickets` array carries one entry per leg. A single-train journey has one entry; a changeover journey has two. The `departureScheduleStopId` and `arrivalScheduleStopId` values come directly from the route search response.

**Overbooking prevention.** Before creating anything, the service counts seats already committed on each requested segment:

```sql
SELECT COUNT(t) FROM Ticket t
WHERE  t.departureScheduleStop.schedule.id = :scheduleId
AND    t.departureScheduleStop.routeStop.stopNumber < :arrivalStopNumber
AND    t.arrivalScheduleStop.routeStop.stopNumber   > :departureStopNumber
```

Two tickets overlap on a segment when their stop-number intervals intersect: `[D1, A1)` overlaps `[D2, A2)` when `D1 < A2` and `D2 < A1`. If the count equals or exceeds the train's seat capacity the booking is rejected with `409` and nothing is written to the database.

**All-or-nothing transaction.** The booking method is annotated `@Transactional`. Capacity is validated for every ticket before the booking record or any ticket record is saved. If the second ticket in a two-ticket booking is sold out, neither the booking nor the first ticket is persisted.

**Confirmation email.** After all tickets are saved a single email is sent to the user listing every ticket:

```
Hello sofia.balaci,

Your booking has been confirmed.

Tickets (2):
  1. Bucuresti Nord → Brasov       | 2026-05-15 | 08:00
  2. Brasov         → Cluj-Napoca  | 2026-05-15 | 12:00

Thank you for travelling with us!
```

---

### Admin — Route and Schedule Management

All endpoints follow standard REST conventions. The complete interactive reference is at `/swagger-ui.html`.

- **Stations** — `GET / POST / DELETE /api/stations`
- **Trains** — `GET / POST / PUT / DELETE /api/trains`
- **Routes** — `GET / POST / PUT / DELETE /api/routes`
- **Route stops** — `GET / POST / PUT / DELETE /api/route-stops` — each entry links a route, a station, and a `stopNumber`
- **Schedules** — `GET / POST / DELETE /api/schedules` — binds a train to a route
- **Schedule stops** — `GET / POST / PUT / DELETE /api/schedule-stops` — assigns concrete arrival and departure timestamps to one route stop within a schedule

---

### Admin — Bookings

- `GET /api/bookings` — all bookings across all users
- `GET /api/bookings/{id}` — single booking with its tickets
- `DELETE /api/bookings/{id}` — cancel a booking

---

### Admin — Delay Reporting

`POST /api/delays`

```json
{
  "scheduleId": 1,
  "fromScheduleStopId": 2,
  "delayMinutes": 20
}
```

The service finds all passengers affected by the delay — those whose ticket on that schedule arrives beyond the delayed stop:

```sql
SELECT t FROM Ticket t
WHERE  t.departureScheduleStop.schedule.id = :scheduleId
AND    t.arrivalScheduleStop.routeStop.stopNumber > :fromStopNumber
```

Each affected user receives an individual email:

```
Hello cristian.alexutan,

We regret to inform you that your train has been delayed by 20 minutes.

From:               Ploiesti Sud
To:                 Brasov
Original departure: 2026-05-15 | 09:05

We apologise for the inconvenience.
```

---

## Tests

The test suite uses JUnit 5 with Mockito. No Spring context is loaded — all dependencies are mocked. Three service classes with non-trivial business logic are covered:

- **RouteSearchServiceTest** — direct route found; wrong direction rejected; no shared schedule; valid changeover; changeover rejected when the connecting departure is before the changeover arrival
- **BookingServiceImplTest** — successful booking; train full; last seat taken; multiple tickets with one confirmation email; second ticket sold out rolls back the entire transaction
- **DelayServiceImplTest** — delay saved and all affected passengers notified; delay saved with no passengers to notify

---

## Setup Guide

### Prerequisites

- Java 25
- PostgreSQL 14 or later
- An SMTP account (e.g. a Gmail app password)

### 1. Create the database

This is the only SQL you need to run manually:

```sql
CREATE DATABASE train_db;
```

Everything else — table creation and seed data — happens automatically on first startup. Hibernate creates the schema via `ddl-auto=update`, then Spring Boot executes `src/main/resources/data.sql`, which uses `ON CONFLICT DO NOTHING` so it is safe to re-run on every subsequent restart without duplicating data.

### 2. Configure application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/train_db
spring.datasource.username=postgres
spring.datasource.password=<your-db-password>

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-gmail-address>
spring.mail.password=<gmail-app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. Run

```bash
./mvnw spring-boot:run
```

The application starts on port 8080. The Swagger UI is at `http://localhost:8080/swagger-ui.html`.

### Default credentials (from seed data)

| Username | Password | Role |
|---|---|---|
| admin | password | ADMIN |
| sofia.balaci | password | CLIENT |
| cristian.alexutan | password | CLIENT |

### Overbooking test

Schedule 4 (train IR 9999, 2 seats) runs on 2026-05-16. The seed pre-fills both seats, so a booking attempt on that segment returns a sold-out error without affecting the main 2026-05-15 data.
