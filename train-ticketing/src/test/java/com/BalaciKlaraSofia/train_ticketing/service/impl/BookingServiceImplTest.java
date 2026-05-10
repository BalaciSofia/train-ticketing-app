package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.*;
import com.BalaciKlaraSofia.train_ticketing.dto.BookingRequest;
import com.BalaciKlaraSofia.train_ticketing.dto.TicketRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.BookingRepository;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private TicketService ticketService;
    @Mock private ScheduleStopService scheduleStopService;
    @Mock private UserService userService;
    @Mock private EmailServiceImpl emailService;

    @InjectMocks private BookingServiceImpl bookingService;

    private BookingRequest request(int userId, int depStopId, int arrStopId) {
        TicketRequest tr = new TicketRequest();
        tr.setDepartureScheduleStopId(depStopId);
        tr.setArrivalScheduleStopId(arrStopId);

        BookingRequest r = new BookingRequest();
        r.setUserId(userId);
        r.setTickets(List.of(tr));
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
    void book_success_returnsBookingAndSendsOneEmail() {
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
        verify(emailService).sendBookingConfirmation(eq(user), anyList());
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
        verifyNoInteractions(emailService);
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
        verify(emailService).sendBookingConfirmation(eq(user), anyList());
    }

    @Test
    void book_multipleTickets_allValidated_oneEmailSent() {
        TicketRequest tr1 = new TicketRequest();
        tr1.setDepartureScheduleStopId(1);
        tr1.setArrivalScheduleStopId(4);

        TicketRequest tr2 = new TicketRequest();
        tr2.setDepartureScheduleStopId(9);
        tr2.setArrivalScheduleStopId(11);

        BookingRequest req = new BookingRequest();
        req.setUserId(1);
        req.setTickets(List.of(tr1, tr2));

        ScheduleStop dep1 = mockDepStop(1, 1, 200);
        ScheduleStop arr1 = mockArrStop(4);
        ScheduleStop dep2 = mockDepStop(3, 1, 300);
        ScheduleStop arr2 = mockArrStop(3);
        User user = mock(User.class);
        Booking savedBooking = mock(Booking.class);

        when(scheduleStopService.getById(1)).thenReturn(Optional.of(dep1));
        when(scheduleStopService.getById(4)).thenReturn(Optional.of(arr1));
        when(scheduleStopService.getById(9)).thenReturn(Optional.of(dep2));
        when(scheduleStopService.getById(11)).thenReturn(Optional.of(arr2));
        when(userService.getById(1)).thenReturn(Optional.of(user));
        when(ticketService.countOverlappingTickets(1, 1, 4)).thenReturn(0L);
        when(ticketService.countOverlappingTickets(3, 1, 3)).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(ticketService.add(any(Ticket.class))).thenReturn(mock(Ticket.class));

        Booking result = bookingService.book(req);

        assertNotNull(result);
        verify(ticketService, times(2)).add(any(Ticket.class));
        verify(emailService, times(1)).sendBookingConfirmation(eq(user), anyList());
    }

    @Test
    void book_secondTicketFull_nothingCreated() {
        TicketRequest tr1 = new TicketRequest();
        tr1.setDepartureScheduleStopId(1);
        tr1.setArrivalScheduleStopId(4);

        TicketRequest tr2 = new TicketRequest();
        tr2.setDepartureScheduleStopId(9);
        tr2.setArrivalScheduleStopId(11);

        BookingRequest req = new BookingRequest();
        req.setUserId(1);
        req.setTickets(List.of(tr1, tr2));

        ScheduleStop dep1 = mockDepStop(1, 1, 200);
        ScheduleStop arr1 = mockArrStop(4);
        ScheduleStop dep2 = mockDepStop(3, 1, 300);
        ScheduleStop arr2 = mockArrStop(3);
        User user = mock(User.class);

        when(scheduleStopService.getById(1)).thenReturn(Optional.of(dep1));
        when(scheduleStopService.getById(4)).thenReturn(Optional.of(arr1));
        when(scheduleStopService.getById(9)).thenReturn(Optional.of(dep2));
        when(scheduleStopService.getById(11)).thenReturn(Optional.of(arr2));
        when(userService.getById(1)).thenReturn(Optional.of(user));
        when(ticketService.countOverlappingTickets(1, 1, 4)).thenReturn(0L);
        when(ticketService.countOverlappingTickets(3, 1, 3)).thenReturn(300L);

        assertThrows(IllegalStateException.class, () -> bookingService.book(req));
        verify(bookingRepository, never()).save(any());
        verifyNoInteractions(emailService);
    }
}
