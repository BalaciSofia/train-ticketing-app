package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Route;
import com.BalaciKlaraSofia.train_ticketing.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public List<Route> getAll() {
        return routeService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Route> getById(@PathVariable Integer id) {
        return routeService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Route add(@RequestBody Route route) {
        return routeService.add(route);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> update(@PathVariable Integer id, @RequestBody Route route) {
        return routeService.getById(id)
                .map(existing -> {
                    route.setId(id);
                    return ResponseEntity.ok(routeService.update(route));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
