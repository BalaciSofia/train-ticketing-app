package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.Booking;
import com.BalaciKlaraSofia.train_ticketing.dto.BookingRequest;
import com.BalaciKlaraSofia.train_ticketing.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAll() {
        return bookingService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getById(@PathVariable Integer id) {
        return bookingService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> book(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.book(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable Integer id, @RequestBody Booking booking) {
        return bookingService.getById(id)
                .map(existing -> {
                    booking.setId(id);
                    return ResponseEntity.ok(bookingService.update(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
