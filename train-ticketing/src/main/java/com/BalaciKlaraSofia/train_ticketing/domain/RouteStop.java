package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "route_stops")
public class RouteStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private Integer stopNumber;

    protected RouteStop() {}

    public RouteStop(Route route, Station station, Integer stopNumber) {
        this.route = route;
        this.station = station;
        this.stopNumber = stopNumber;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Route getRoute() { return route; }
    public Station getStation() { return station; }
    public Integer getStopNumber() { return stopNumber; }

    public void setRoute(Route route) { this.route = route; }
    public void setStation(Station station) { this.station = station; }
    public void setStopNumber(Integer stopNumber) { this.stopNumber = stopNumber; }
}
