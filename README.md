# Train Ticketing Application

A Spring Boot REST application for managing train schedules, bookings, and delay notifications. Supports two roles: client (search routes, book tickets) and admin (manage the full catalogue, report delays).

## Setup Guide

### Prerequisites

- Java 25 JDK — [Download from jdk.java.net/25](https://jdk.java.net/25/)
- Docker Desktop — [Download from docker.com](https://www.docker.com/products/docker-desktop/)

No Maven installation needed — the Maven wrapper (`mvnw`) handles it automatically.

### 1. Clone the repository

```bash
git clone https://github.com/BalaciSofia/train-ticketing-app.git
cd train-ticketing-app
```

### 2. Start the database

From the root of the repository, start a PostgreSQL container with Docker Compose:

```bash
docker compose -f deploy/docker-compose.yml up -d
```

This starts PostgreSQL 16 on port `5432` with the `train_db` database already created.

### 3. Start the application

Navigate into the project folder and run:

```bash
cd train-ticketing
./mvnw spring-boot:run
```

Wait until you see `Started TrainTicketingApplication` in the logs. Hibernate creates all tables automatically on first boot.

### 4. Seed the database

Once the app has started and created the schema, stop it (`Ctrl+C`), go back to the root directory and run the seed script to populate stations, routes, trains, and demo users.

On Linux / macOS:

```bash
psql -U postgres -d train_db -f plan/seed.sql
```

On Windows PowerShell (no psql installation needed):

```powershell
Get-Content plan\seed.sql | docker exec -i train_ticketing_db psql -U postgres -d train_db
```

### 5. Start the application again

```bash
cd train-ticketing
./mvnw spring-boot:run
```

The application is now running at `http://localhost:8080`.

### 6. Open the UI

| Page | URL |
|---|---|
| Login | http://localhost:8080/login.html |
| Register | http://localhost:8080/register.html |
| User dashboard | http://localhost:8080/user.html |
| Admin panel | http://localhost:8080/admin.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |

### Default credentials

| Username | Password | Role |
|---|---|---|
| admin | password123 | ADMIN |
| sofia.balaci | password123 | CLIENT |
| cristian.alexutan | password123 | CLIENT |

### 7. Stopping the database

```bash
docker compose -f deploy/docker-compose.yml down
```

Data is persisted in a Docker volume and survives restarts. To wipe everything and start fresh:

```bash
docker compose -f deploy/docker-compose.yml down -v
```

## Technology Stack

- Java 25 / Spring Boot 4.0.6
- Spring Data JPA (Hibernate) with PostgreSQL
- Spring Security Crypto - BCrypt password hashing
- Spring Boot Mail - SMTP email via JavaMailSender
- springdoc-openapi - Swagger UI auto-generated at `/swagger-ui.html`
- JUnit 5 + Mockito - unit tests


## Database Schema

The full schema and seed data are in the plan directory. Running that script against a fresh PostgreSQL database gives a working dataset for development and testing.

The domain consists of ten tables:

- **stations** - physical stops; each has a city name
- **routes** - named sequences of stations (e.g. Bucuresti - Brasov)
- **route_stops** - junction between a route and a station, with a `stop_number` defining the travel order
- **trains** - physical assets with a seat capacity and a number (e.g. IR 1581)
- **schedules** - bind a train to a route for a specific run
- **schedule_stops** - the timetable entry for one stop within a schedule, storing both arrival and departure timestamps independently 
- **users** - application accounts; passwords stored exclusively as BCrypt hashes
- **bookings** - groups one or more tickets under one transaction for a single user
- **tickets** - one seat on one schedule between two `schedule_stops` (departure and arrival)
- **delays** - a reported delay on a schedule starting from a given stop, used to drive email notifications

The central relationship is `schedule_stop`: it sits at the intersection of a `schedule` (which train, which route) and a `route_stop` (which station, in what position). A ticket references two `schedule_stops` on the same schedule - its board and alight points.


## Functionalities

### Authentication

**Login** - `POST /api/auth/login`

```json
{ "username": "sofia.balaci", "password": "password123" }
```

Returns `200` with `{ "id", "username", "role" }` on success, `401` on bad credentials.

**Register** - `POST /api/auth/register`

```json
{ "username": "ion.popescu", "email": "ion@example.com", "password": "mypassword" }
```

Returns `201` on success. Returns `409` if the username or email is already taken. New accounts are always created with the `CLIENT` role; admin accounts must be inserted directly in the database.
This is not a realistic setup, it s present to showcase the different roles. The register functionality specifically is for other users to try out the app and recive the appropriate emails(delay+booking).


### Route Search

`GET /api/search?fromStationId={id}&toStationId={id}`

Returns all connections between the two stations across every schedule, split into direct routes and routes with one changeover. No date filtering is applied at the API level - every scheduled run that matches is returned, and callers can filter by date on their end.

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

If no connection exists the response contains empty arrays.

**Algorithm**

1. **Direct routes.** Load all `schedule_stops` at the departure station and all at the arrival station. Group both sets by `schedule_id`. For every schedule present in both groups, check that the departure `stop_number` is lower than the arrival `stop_number` (ensuring the correct direction of travel). Each match becomes a `DirectRoute`.

2. **Changeover routes.** For each departure-station stop that did not produce a direct route, fetch all later stops on the same schedule (`findLaterStopsOnSchedule`). Each later stop is a candidate changeover station. For each candidate, find all `schedule_stops` at that station belonging to a *different* schedule. If any of those stops belongs to a schedule that also serves the final destination (with correct stop ordering), and the connecting departure is strictly after the changeover arrival, the two legs form a `ChangeoverRoute`.

The current implementation supports at most one changeover. To support N changeovers the natural extension is a graph search.

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

**Overbooking prevention.** Before creating anything, the service counts seats already committed on each requested segment. Two tickets overlap on a segment when their stop-number intervals intersect. If the count equals or exceeds the train's seat capacity the booking is rejected with `409` and nothing is written to the database.

The booking method is annotated `@Transactional`. Capacity is validated for every ticket before the booking record or any ticket record is saved. If the second ticket in a two-ticket booking is sold out, neither the booking nor the first ticket is persisted.

After all tickets are saved a single email is sent to the user listing every ticket:

```
Hello sofia.balaci,

Your booking has been confirmed.

Tickets (2):
  1. Bucuresti Nord -> Brasov       | 2026-05-15 | 08:00
  2. Brasov         -> Cluj-Napoca  | 2026-05-15 | 12:00

Thank you for travelling with us!
```

### Admin - Route and Schedule Management

All endpoints follow standard REST conventions. The complete interactive reference is at `/swagger-ui.html`.

- **Stations** - `GET / POST / DELETE /api/stations`
- **Trains** - `GET / POST / PUT / DELETE /api/trains`
- **Routes** - `GET / POST / PUT / DELETE /api/routes`
- **Route stops** - `GET / POST / PUT / DELETE /api/route-stops` - each entry links a route, a station, and a `stopNumber`
- **Schedules** - `GET / POST / DELETE /api/schedules` - binds a train to a route
- **Schedule stops** - `GET / POST / PUT / DELETE /api/schedule-stops` - assigns concrete arrival and departure timestamps to one route stop within a schedule


### Admin - Bookings

- `GET /api/bookings` - all bookings across all users
- `GET /api/bookings/{id}` - single booking with its tickets
- `DELETE /api/bookings/{id}` - cancel a booking


### Admin - Delay Reporting

`POST /api/delays`

```json
{
  "scheduleId": 1,
  "fromScheduleStopId": 2,
  "delayMinutes": 20
}
```
The delay is considered to occur before arriving at the stop @fromScheduleStopId.
The service finds all passengers affected by the delay - those whose ticket on that schedule arrives beyond the delayed stop.
Each affected user receives an individual email:

```
Hello balaci.sofia,

We regret to inform you that your train has been delayed by 20 minutes.

From:               Ploiesti Sud
To:                 Brasov
Original departure: 2026-05-15 | 09:05

We apologise for the inconvenience.
```


## Tests

The test suite uses JUnit 5 with Mockito. No Spring context is loaded - all dependencies are mocked. Three service classes with non-trivial business logic are covered: RouteSearchServiceTest, BookingServiceImplTest, DelayServiceImplTest

