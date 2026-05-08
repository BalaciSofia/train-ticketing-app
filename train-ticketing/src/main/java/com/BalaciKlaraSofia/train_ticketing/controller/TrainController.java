package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Train;
import com.BalaciKlaraSofia.train_ticketing.service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping
    public List<Train> getAll() {
        return trainService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Train> getById(@PathVariable Integer id) {
        return trainService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Train add(@RequestBody Train train) {
        return trainService.add(train);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Train> update(@PathVariable Integer id, @RequestBody Train train) {
        return trainService.getById(id)
                .map(existing -> {
                    train.setId(id);
                    return ResponseEntity.ok(trainService.update(train));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        trainService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
