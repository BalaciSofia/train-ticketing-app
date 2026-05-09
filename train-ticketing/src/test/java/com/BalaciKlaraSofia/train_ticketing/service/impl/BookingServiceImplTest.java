package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.*;
import com.BalaciKlaraSofia.train_ticketing.dto.BookingRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.BookingRepository;
import com.BalaciKlaraSofia.train_ticketing.service.EmailService;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private TicketService ticketService;
    @Mock private ScheduleStopService scheduleStopService;
    @Mock private UserService userService;
    @Mock private EmailService emailService;

    @InjectMocks private BookingServiceImpl bookingService;

    private BookingRequest request(int userId, int depStopId, int arrStopId) {
        BookingRequest r = new BookingRequest();
        r.setUserId(userId);
        r.setDepartureScheduleStopId(depStopId);
        r.setArrivalScheduleStopId(arrStopId);
        return r;
    }

    private ScheduleStop mockDepStop(int scheduleId, int stopNumber, int totalSeats) {
        Train train = mock(Train.class);
        when(train.getNumberOfSeats()).thenReturn(totalSeats);

        Schedule schedule = mock(Schedule.class);
        when(schedule.getId()).thenReturn(scheduleId);
        when(schedule.getTrain()).thenReturn(train);

        RouteStop routeStop = mock(RouteStop.class);
        when(routeStop.getStopNumber()).thenReturn(stopNumber);

        ScheduleStop stop = mock(ScheduleStop.class);
        when(stop.getSchedule()).thenReturn(schedule);
        when(stop.getRouteStop()).thenReturn(routeStop);
        return stop;
    }

    private ScheduleStop mockArrStop(int stopNumber) {
        RouteStop routeStop = mock(RouteStop.class);
        when(routeStop.getStopNumber()).thenReturn(stopNumber);

        ScheduleStop stop = mock(ScheduleStop.class);
        when(stop.getRouteStop()).thenReturn(routeStop);
        return stop;
    }

    @Test
    void book_success_returnsBookingAndSendsEmail() {
        BookingRequest req = request(1, 1, 4);

        ScheduleStop depStop = mockDepStop(1, 1, 200);
        ScheduleStop arrStop = mockArrStop(4);
        User user = mock(User.class);
        Booking savedBooking = mock(Booking.class);
        Ticket savedTicket = mock(Ticket.class);

        when(scheduleStopService.getById(1)).thenReturn(Optional.of(depStop));
        when(scheduleStopService.getById(4)).thenReturn(Optional.of(arrStop));
        when(userService.getById(1)).thenReturn(Optional.of(user));
        when(ticketService.countOverlappingTickets(1, 1, 4)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(ticketService.add(any(Ticket.class))).thenReturn(savedTicket);

        Booking result = bookingService.book(req);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
        verify(ticketService).add(any(Ticket.class));
        verify(emailService).sendBookingConfirmation(user, savedTicket);
    }

    @Test
    void book_trainFull_throwsIllegalStateException() {
        BookingRequest req = request(1, 1, 4);

        ScheduleStop depStop = mockDepStop(1, 1, 200);
        ScheduleStop arrStop = mockArrStop(4);
        User user = mock(User.class);

        when(scheduleStopService.getById(1)).thenReturn(Optional.of(depStop));
        when(scheduleStopService.getById(4)).thenReturn(Optional.of(arrStop));
        when(userService.getById(1)).thenReturn(Optional.of(user));
        when(ticketService.countOverlappingTickets(1, 1, 4)).thenReturn(200L);

        assertThrows(IllegalStateException.class, () -> bookingService.book(req));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void book_lastSeatAvailable_succeeds() {
        BookingRequest req = request(1, 1, 4);

        ScheduleStop depStop = mockDepStop(1, 1, 200);
        ScheduleStop arrStop = mockArrStop(4);
        User user = mock(User.class);
        Booking savedBooking = mock(Booking.class);
        Ticket savedTicket = mock(Ticket.class);

        when(scheduleStopService.getById(1)).thenReturn(Optional.of(depStop));
        when(scheduleStopService.getById(4)).thenReturn(Optional.of(arrStop));
        when(userService.getById(1)).thenReturn(Optional.of(user));
        when(ticketService.countOverlappingTickets(1, 1, 4)).thenReturn(199L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(ticketService.add(any(Ticket.class))).thenReturn(savedTicket);

        Booking result = bookingService.book(req);

        assertNotNull(result);
        verify(bookingRepository).save(any(Booking.class));
    }
}
