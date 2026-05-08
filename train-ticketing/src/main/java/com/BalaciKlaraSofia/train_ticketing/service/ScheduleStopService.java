package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;

import java.util.List;
import java.util.Optional;

public interface ScheduleStopService {
    List<ScheduleStop> getAll();
    Optional<ScheduleStop> getById(Integer id);
    ScheduleStop add(ScheduleStop scheduleStop);
    ScheduleStop update(ScheduleStop scheduleStop);
    void delete(Integer id);
}
