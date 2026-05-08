package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Delay;
import com.BalaciKlaraSofia.train_ticketing.service.DelayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delays")
public class DelayController {

    private final DelayService delayService;

    public DelayController(DelayService delayService) {
        this.delayService = delayService;
    }

    @GetMapping
    public List<Delay> getAll() {
        return delayService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delay> getById(@PathVariable Integer id) {
        return delayService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Delay add(@RequestBody Delay delay) {
        return delayService.add(delay);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Delay> update(@PathVariable Integer id, @RequestBody Delay delay) {
        return delayService.getById(id)
                .map(existing -> {
                    delay.setId(id);
                    return ResponseEntity.ok(delayService.update(delay));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        delayService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
