package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleStopRepository extends JpaRepository<ScheduleStop, Integer> {

    @Query("SELECT ss FROM ScheduleStop ss WHERE ss.routeStop.station.id = :stationId")
    List<ScheduleStop> findByStationId(@Param("stationId") Integer stationId);

    @Query("SELECT ss FROM ScheduleStop ss WHERE ss.schedule.id = :scheduleId AND ss.routeStop.stopNumber > :stopNumber")
    List<ScheduleStop> findLaterStopsOnSchedule(@Param("scheduleId") Integer scheduleId, @Param("stopNumber") Integer stopNumber);

    @Query("SELECT ss FROM ScheduleStop ss WHERE ss.schedule.id = :scheduleId")
    List<ScheduleStop> findByScheduleId(@Param("scheduleId") Integer scheduleId);
}
