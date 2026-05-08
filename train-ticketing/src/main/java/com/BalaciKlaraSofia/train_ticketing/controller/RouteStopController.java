package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.RouteStop;
import com.BalaciKlaraSofia.train_ticketing.service.RouteStopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route-stops")
public class RouteStopController {

    private final RouteStopService routeStopService;

    public RouteStopController(RouteStopService routeStopService) {
        this.routeStopService = routeStopService;
    }

    @GetMapping
    public List<RouteStop> getAll() {
        return routeStopService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteStop> getById(@PathVariable Integer id) {
        return routeStopService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RouteStop add(@RequestBody RouteStop routeStop) {
        return routeStopService.add(routeStop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteStop> update(@PathVariable Integer id, @RequestBody RouteStop routeStop) {
        return routeStopService.getById(id)
                .map(existing -> {
                    routeStop.setId(id);
                    return ResponseEntity.ok(routeStopService.update(routeStop));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        routeStopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
