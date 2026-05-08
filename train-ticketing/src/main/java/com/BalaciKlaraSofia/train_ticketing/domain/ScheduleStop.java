package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_stops")
public class ScheduleStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer scheduleId;

    @Column(nullable = false)
    private Integer routeStopId;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    protected ScheduleStop() {}

    public ScheduleStop(Integer scheduleId, Integer routeStopId, LocalDateTime arrivalTime, LocalDateTime departureTime) {
        this.scheduleId = scheduleId;
        this.routeStopId = routeStopId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    public Integer getId() { return id; }
    public Integer getScheduleId() { return scheduleId; }
    public Integer getRouteStopId() { return routeStopId; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public LocalDateTime getDepartureTime() { return departureTime; }

    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public void setRouteStopId(Integer routeStopId) { this.routeStopId = routeStopId; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
}
