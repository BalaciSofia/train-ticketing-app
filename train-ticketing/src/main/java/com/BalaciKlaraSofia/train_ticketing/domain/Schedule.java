package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer routeId;

    @Column(nullable = false)
    private Integer trainId;

    protected Schedule() {}

    public Schedule(Integer routeId, Integer trainId) {
        this.routeId = routeId;
        this.trainId = trainId;
    }

    public Integer getId() { return id; }
    public Integer getRouteId() { return routeId; }
    public Integer getTrainId() { return trainId; }

    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public void setTrainId(Integer trainId) { this.trainId = trainId; }
}
