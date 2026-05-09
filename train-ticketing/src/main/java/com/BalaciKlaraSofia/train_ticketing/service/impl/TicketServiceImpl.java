package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.repository.TicketRepository;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Optional<Ticket> getById(Integer id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket add(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Integer id) {
        ticketRepository.deleteById(id);
    }
    @Override
    public long countOverlappingTickets(Integer scheduleId, Integer departureStopNumber, Integer arrivalStopNumber) {
        return ticketRepository.countOverlappingTickets(scheduleId, departureStopNumber, arrivalStopNumber);
    }
}
