package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Station;
import com.BalaciKlaraSofia.train_ticketing.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationRepository stations;

    public StationController(StationRepository stations) {
        this.stations = stations;
    }

    @GetMapping
    public List<Station> all() {
        return stations.findAll();
    }

    @PostMapping
    public Station create(@RequestBody Station station) {
        return stations.save(station);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        stations.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}