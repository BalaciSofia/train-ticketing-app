package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleStopRepository extends JpaRepository<ScheduleStop, Integer> {
}
