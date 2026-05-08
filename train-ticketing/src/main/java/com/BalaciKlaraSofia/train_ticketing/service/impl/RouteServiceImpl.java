package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Route;
import com.BalaciKlaraSofia.train_ticketing.repository.RouteRepository;
import com.BalaciKlaraSofia.train_ticketing.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public List<Route> getAll() {
        return routeRepository.findAll();
    }

    @Override
    public Optional<Route> getById(Integer id) {
        return routeRepository.findById(id);
    }

    @Override
    public Route add(Route route) {
        return routeRepository.save(route);
    }

    @Override
    public Route update(Route route) {
        return routeRepository.save(route);
    }

    @Override
    public void delete(Integer id) {
        routeRepository.deleteById(id);
    }
}
