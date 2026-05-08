package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer routeId;

    @Column(nullable = false)
    private Integer stationId;

    @Column(nullable = false)
    private Integer stopNumber;

    protected RouteStop() {}

    public RouteStop(Integer routeId, Integer stationId, Integer stopNumber) {
        this.routeId = routeId;
        this.stationId = stationId;
        this.stopNumber = stopNumber;
    }

    public Integer getId() { return id; }
    public Integer getRouteId() { return routeId; }
    public Integer getStationId() { return stationId; }
    public Integer getStopNumber() { return stopNumber; }

    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public void setStationId(Integer stationId) { this.stationId = stationId; }
    public void setStopNumber(Integer stopNumber) { this.stopNumber = stopNumber; }
}
