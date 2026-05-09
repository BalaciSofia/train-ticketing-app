package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    protected Schedule() {}

    public Schedule(Route route, Train train) {
        this.route = route;
        this.train = train;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Route getRoute() { return route; }
    public Train getTrain() { return train; }

    public void setRoute(Route route) { this.route = route; }
    public void setTrain(Train train) { this.train = train; }
}
