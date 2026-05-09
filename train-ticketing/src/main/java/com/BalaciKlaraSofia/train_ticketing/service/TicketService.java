package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<Ticket> getAll();
    Optional<Ticket> getById(Integer id);
    Ticket add(Ticket ticket);
    Ticket update(Ticket ticket);
    void delete(Integer id);
    long countOverlappingTickets(Integer scheduleId, Integer departureStopNumber, Integer arrivalStopNumber);

    List<Ticket> findByScheduleId(Integer scheduleId);
    List<Ticket> findByTrainId(Integer trainId);
    List<Ticket> findAffectedByDelay(Integer scheduleId, Integer fromStopNumber);

}
