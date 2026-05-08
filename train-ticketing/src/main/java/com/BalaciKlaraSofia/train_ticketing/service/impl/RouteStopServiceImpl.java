package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.RouteStop;
import com.BalaciKlaraSofia.train_ticketing.repository.RouteStopRepository;
import com.BalaciKlaraSofia.train_ticketing.service.RouteStopService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteStopServiceImpl implements RouteStopService {

    private final RouteStopRepository routeStopRepository;

    public RouteStopServiceImpl(RouteStopRepository routeStopRepository) {
        this.routeStopRepository = routeStopRepository;
    }

    @Override
    public List<RouteStop> getAll() {
        return routeStopRepository.findAll();
    }

    @Override
    public Optional<RouteStop> getById(Integer id) {
        return routeStopRepository.findById(id);
    }

    @Override
    public RouteStop add(RouteStop routeStop) {
        return routeStopRepository.save(routeStop);
    }

    @Override
    public RouteStop update(RouteStop routeStop) {
        return routeStopRepository.save(routeStop);
    }

    @Override
    public void delete(Integer id) {
        routeStopRepository.deleteById(id);
    }
}
