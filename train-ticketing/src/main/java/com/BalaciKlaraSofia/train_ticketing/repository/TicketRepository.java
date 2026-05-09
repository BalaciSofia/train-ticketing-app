package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.departureScheduleStop.schedule.id = :scheduleId")
    List<Ticket> findByScheduleId(@Param("scheduleId") Integer scheduleId);

    @Query("SELECT t FROM Ticket t WHERE t.departureScheduleStop.schedule.train.id = :trainId")
    List<Ticket> findByTrainId(@Param("trainId") Integer trainId);

    @Query("SELECT t FROM Ticket t " +
           "WHERE t.departureScheduleStop.schedule.id = :scheduleId " +
           "AND t.arrivalScheduleStop.routeStop.stopNumber > :fromStopNumber")
    List<Ticket> findAffectedByDelay(@Param("scheduleId") Integer scheduleId,
                                     @Param("fromStopNumber") Integer fromStopNumber);

    @Query("SELECT COUNT(t) FROM Ticket t " +
           "WHERE t.departureScheduleStop.schedule.id = :scheduleId " +
           "AND t.departureScheduleStop.routeStop.stopNumber < :arrivalStopNumber " +
           "AND t.arrivalScheduleStop.routeStop.stopNumber > :departureStopNumber")
    long countOverlappingTickets(@Param("scheduleId") Integer scheduleId,
                                 @Param("departureStopNumber") Integer departureStopNumber,
                                 @Param("arrivalStopNumber") Integer arrivalStopNumber);
}
