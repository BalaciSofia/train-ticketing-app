package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAll() {
        return ticketService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Integer id) {
        return ticketService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Ticket add(@RequestBody Ticket ticket) {
        return ticketService.add(ticket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable Integer id, @RequestBody Ticket ticket) {
        return ticketService.getById(id)
                .map(existing -> {
                    ticket.setId(id);
                    return ResponseEntity.ok(ticketService.update(ticket));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
