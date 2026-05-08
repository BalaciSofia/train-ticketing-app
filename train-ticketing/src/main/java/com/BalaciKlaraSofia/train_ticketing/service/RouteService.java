package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Route;

import java.util.List;
import java.util.Optional;

public interface RouteService {
    List<Route> getAll();
    Optional<Route> getById(Integer id);
    Route add(Route route);
    Route update(Route route);
    void delete(Integer id);
}
