package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query("SELECT COUNT(t) FROM Ticket t " +
           "WHERE t.departureScheduleStop.schedule.id = :scheduleId " +
           "AND t.departureScheduleStop.routeStop.stopNumber < :arrivalStopNumber " +
           "AND t.arrivalScheduleStop.routeStop.stopNumber > :departureStopNumber")
    long countOverlappingTickets(@Param("scheduleId") Integer scheduleId,
                                 @Param("departureStopNumber") Integer departureStopNumber,
                                 @Param("arrivalStopNumber") Integer arrivalStopNumber);
}
