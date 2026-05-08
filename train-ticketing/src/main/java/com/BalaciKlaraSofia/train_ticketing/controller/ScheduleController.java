package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Schedule;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<Schedule> getAll() {
        return scheduleService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getById(@PathVariable Integer id) {
        return scheduleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Schedule add(@RequestBody Schedule schedule) {
        return scheduleService.add(schedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> update(@PathVariable Integer id, @RequestBody Schedule schedule) {
        return scheduleService.getById(id)
                .map(existing -> {
                    schedule.setId(id);
                    return ResponseEntity.ok(scheduleService.update(schedule));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
