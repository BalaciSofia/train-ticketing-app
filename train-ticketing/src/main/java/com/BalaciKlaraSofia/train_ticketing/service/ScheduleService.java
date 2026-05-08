package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {
    List<Schedule> getAll();
    Optional<Schedule> getById(Integer id);
    Schedule add(Schedule schedule);
    Schedule update(Schedule schedule);
    void delete(Integer id);
}
