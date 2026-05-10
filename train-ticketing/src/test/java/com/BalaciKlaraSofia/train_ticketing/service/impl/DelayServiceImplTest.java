package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.*;
import com.BalaciKlaraSofia.train_ticketing.dto.DelayRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.DelayRepository;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleService;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DelayServiceImplTest {

    @Mock private DelayRepository delayRepository;
    @Mock private ScheduleService scheduleService;
    @Mock private ScheduleStopService scheduleStopService;
    @Mock private TicketService ticketService;
    @Mock private EmailServiceImpl emailService;

    @InjectMocks private DelayServiceImpl delayService;

    private DelayRequest request(int scheduleId, int fromStopId, int delayMinutes) {
        DelayRequest r = new DelayRequest();
        r.setScheduleId(scheduleId);
        r.setFromScheduleStopId(fromStopId);
        r.setDelayMinutes(delayMinutes);
        return r;
    }

    private ScheduleStop mockFromStop(int stopNumber) {
        RouteStop routeStop = mock(RouteStop.class);
        when(routeStop.getStopNumber()).thenReturn(stopNumber);

        ScheduleStop stop = mock(ScheduleStop.class);
        when(stop.getRouteStop()).thenReturn(routeStop);
        return stop;
    }

    private Ticket mockTicketWithUser(User user) {
        Booking booking = mock(Booking.class);
        when(booking.getUser()).thenReturn(user);

        Ticket ticket = mock(Ticket.class);
        when(ticket.getBooking()).thenReturn(booking);
        return ticket;
    }

    @Test
    void report_success_savesDelayAndNotifiesAffectedPassengers() {
        Schedule schedule = mock(Schedule.class);
        ScheduleStop fromStop = mockFromStop(3);
        Delay savedDelay = mock(Delay.class);

        User user1 = mock(User.class);
        User user2 = mock(User.class);
        Ticket ticket1 = mockTicketWithUser(user1);
        Ticket ticket2 = mockTicketWithUser(user2);

        when(scheduleService.getById(1)).thenReturn(Optional.of(schedule));
        when(scheduleStopService.getById(2)).thenReturn(Optional.of(fromStop));
        when(delayRepository.save(any(Delay.class))).thenReturn(savedDelay);
        when(ticketService.findAffectedByDelay(1, 3)).thenReturn(List.of(ticket1, ticket2));
        Delay result = delayService.report(request(1, 2, 20));

        assertNotNull(result);
        verify(delayRepository).save(any(Delay.class));
        verify(emailService).sendDelayNotification(user1, ticket1, 20);
        verify(emailService).sendDelayNotification(user2, ticket2, 20);
    }

    @Test
    void report_noAffectedPassengers_savesDelayAndSendsNoEmails() {
        Schedule schedule = mock(Schedule.class);
        ScheduleStop fromStop = mockFromStop(5);
        Delay savedDelay = mock(Delay.class);

        when(scheduleService.getById(1)).thenReturn(Optional.of(schedule));
        when(scheduleStopService.getById(2)).thenReturn(Optional.of(fromStop));
        when(delayRepository.save(any(Delay.class))).thenReturn(savedDelay);
        when(ticketService.findAffectedByDelay(1, 5)).thenReturn(List.of());

        Delay result = delayService.report(request(1, 2, 10));

        assertNotNull(result);
        verify(delayRepository).save(any(Delay.class));
        verifyNoInteractions(emailService);
    }
}
