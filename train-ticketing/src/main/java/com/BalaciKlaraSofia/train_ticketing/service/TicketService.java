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
}
