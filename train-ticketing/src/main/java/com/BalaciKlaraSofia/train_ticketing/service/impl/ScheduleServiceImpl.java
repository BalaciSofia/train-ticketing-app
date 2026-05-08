package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Schedule;
import com.BalaciKlaraSofia.train_ticketing.repository.ScheduleRepository;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Optional<Schedule> getById(Integer id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public Schedule add(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule update(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public void delete(Integer id) {
        scheduleRepository.deleteById(id);
    }
}
