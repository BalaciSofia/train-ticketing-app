package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import com.BalaciKlaraSofia.train_ticketing.repository.ScheduleStopRepository;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleStopServiceImpl implements ScheduleStopService {

    private final ScheduleStopRepository scheduleStopRepository;

    public ScheduleStopServiceImpl(ScheduleStopRepository scheduleStopRepository) {
        this.scheduleStopRepository = scheduleStopRepository;
    }

    @Override
    public List<ScheduleStop> getAll() {
        return scheduleStopRepository.findAll();
    }

    @Override
    public Optional<ScheduleStop> getById(Integer id) {
        return scheduleStopRepository.findById(id);
    }

    @Override
    public ScheduleStop add(ScheduleStop scheduleStop) {
        return scheduleStopRepository.save(scheduleStop);
    }

    @Override
    public ScheduleStop update(ScheduleStop scheduleStop) {
        return scheduleStopRepository.save(scheduleStop);
    }

    @Override
    public void delete(Integer id) {
        scheduleStopRepository.deleteById(id);
    }
}
