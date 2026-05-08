package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Station;
import com.BalaciKlaraSofia.train_ticketing.service.StationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<Station> getAll() {
        return stationService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getById(@PathVariable Integer id) {
        return stationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Station add(@RequestBody Station station) {
        return stationService.add(station);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Integer id, @RequestBody Station station) {
        return stationService.getById(id)
                .map(existing -> {
                    station.setId(id);
                    return ResponseEntity.ok(stationService.update(station));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
