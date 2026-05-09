package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule-stops")
public class ScheduleStopController {

    private final ScheduleStopService scheduleStopService;

    public ScheduleStopController(ScheduleStopService scheduleStopService) {
        this.scheduleStopService = scheduleStopService;
    }

    @GetMapping
    public List<ScheduleStop> getAll(@RequestParam(required = false) Integer scheduleId) {
        if (scheduleId != null) {
            return scheduleStopService.findByScheduleId(scheduleId);
        }
        return scheduleStopService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleStop> getById(@PathVariable Integer id) {
        return scheduleStopService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ScheduleStop add(@RequestBody ScheduleStop scheduleStop) {
        return scheduleStopService.add(scheduleStop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleStop> update(@PathVariable Integer id, @RequestBody ScheduleStop scheduleStop) {
        return scheduleStopService.getById(id)
                .map(existing -> {
                    scheduleStop.setId(id);
                    return ResponseEntity.ok(scheduleStopService.update(scheduleStop));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        scheduleStopService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
