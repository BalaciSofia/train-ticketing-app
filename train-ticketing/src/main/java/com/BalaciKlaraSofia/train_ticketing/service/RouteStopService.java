package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.RouteStop;

import java.util.List;
import java.util.Optional;

public interface RouteStopService {
    List<RouteStop> getAll();
    Optional<RouteStop> getById(Integer id);
    RouteStop add(RouteStop routeStop);
    RouteStop update(RouteStop routeStop);
    void delete(Integer id);
}
