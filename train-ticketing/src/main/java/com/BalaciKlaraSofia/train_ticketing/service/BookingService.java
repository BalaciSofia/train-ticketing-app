package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Booking;
import com.BalaciKlaraSofia.train_ticketing.dto.BookingRequest;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAll();
    Optional<Booking> getById(Integer id);
    Booking book(BookingRequest request);
    Booking update(Booking booking);
    void delete(Integer id);
}
