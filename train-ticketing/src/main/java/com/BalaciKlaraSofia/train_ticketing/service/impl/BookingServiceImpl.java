package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.*;
import com.BalaciKlaraSofia.train_ticketing.dto.BookingRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.BookingRepository;
import com.BalaciKlaraSofia.train_ticketing.service.BookingService;
import com.BalaciKlaraSofia.train_ticketing.service.EmailService;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final TicketService ticketService;
    private final ScheduleStopService scheduleStopService;
    private final UserService userService;
    private final EmailService emailService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              TicketService ticketService,
                              ScheduleStopService scheduleStopService,
                              UserService userService,
                              EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.ticketService = ticketService;
        this.scheduleStopService = scheduleStopService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @Override
    public List<Booking> getAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getById(Integer id) {
        return bookingRepository.findById(id);
    }

    @Override
    @Transactional
    public Booking book(BookingRequest request) {
        ScheduleStop depStop = scheduleStopService.getById(request.getDepartureScheduleStopId())
                .orElseThrow(() -> new IllegalArgumentException("Departure stop not found"));
        ScheduleStop arrStop = scheduleStopService.getById(request.getArrivalScheduleStopId())
                .orElseThrow(() -> new IllegalArgumentException("Arrival stop not found"));
        User user = userService.getById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        int capacity = depStop.getSchedule().getTrain().getNumberOfSeats();
        long occupied = ticketService.countOverlappingTickets(
                depStop.getSchedule().getId(),
                depStop.getRouteStop().getStopNumber(),
                arrStop.getRouteStop().getStopNumber()
        );

        if (occupied >= capacity) {
            throw new IllegalStateException("No more seats available on this segment");
        }

        Booking booking = bookingRepository.save(new Booking(user));
        Ticket ticket = ticketService.add(new Ticket(booking, depStop, arrStop));
        emailService.sendBookingConfirmation(user, ticket);
        return booking;
    }

    @Override
    public Booking update(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public void delete(Integer id) {
        bookingRepository.deleteById(id);
    }
}
