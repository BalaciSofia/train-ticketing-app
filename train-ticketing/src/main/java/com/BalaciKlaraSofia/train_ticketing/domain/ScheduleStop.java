package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_stops")
public class ScheduleStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "route_stop_id", nullable = false)
    private RouteStop routeStop;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    protected ScheduleStop() {}

    public ScheduleStop(Schedule schedule, RouteStop routeStop, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.schedule = schedule;
        this.routeStop = routeStop;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Schedule getSchedule() { return schedule; }
    public RouteStop getRouteStop() { return routeStop; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public LocalDateTime getDepartureTime() { return departureTime; }

    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    public void setRouteStop(RouteStop routeStop) { this.routeStop = routeStop; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
}
