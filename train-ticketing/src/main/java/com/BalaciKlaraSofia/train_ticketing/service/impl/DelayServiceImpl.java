package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Delay;
import com.BalaciKlaraSofia.train_ticketing.domain.Schedule;
import com.BalaciKlaraSofia.train_ticketing.domain.ScheduleStop;
import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.dto.DelayRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.DelayRepository;
import com.BalaciKlaraSofia.train_ticketing.exception.NotFoundException;
import com.BalaciKlaraSofia.train_ticketing.service.DelayService;
import com.BalaciKlaraSofia.train_ticketing.service.EmailService;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleService;
import com.BalaciKlaraSofia.train_ticketing.service.ScheduleStopService;
import com.BalaciKlaraSofia.train_ticketing.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DelayServiceImpl implements DelayService {

    private static final Logger log = LoggerFactory.getLogger(DelayServiceImpl.class);

    private final DelayRepository delayRepository;
    private final ScheduleService scheduleService;
    private final ScheduleStopService scheduleStopService;
    private final TicketService ticketService;
    private final EmailService emailService;

    public DelayServiceImpl(DelayRepository delayRepository,
                            ScheduleService scheduleService,
                            ScheduleStopService scheduleStopService,
                            TicketService ticketService,
                            EmailService emailService) {
        this.delayRepository = delayRepository;
        this.scheduleService = scheduleService;
        this.scheduleStopService = scheduleStopService;
        this.ticketService = ticketService;
        this.emailService = emailService;
    }

    @Override
    public List<Delay> getAll() {
        return delayRepository.findAll();
    }

    @Override
    public Optional<Delay> getById(Integer id) {
        return delayRepository.findById(id);
    }

    @Override
    public Delay report(DelayRequest request) {
        Schedule schedule = scheduleService.getById(request.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Schedule not found"));

        ScheduleStop fromStop = scheduleStopService.getById(request.getFromScheduleStopId())
                .orElseThrow(() -> new NotFoundException("From stop not found"));

        Delay delay = delayRepository.save(new Delay(schedule, fromStop, request.getDelayMinutes()));

        int fromStopNumber = fromStop.getRouteStop().getStopNumber();
        List<Ticket> affectedTickets = ticketService.findAffectedByDelay(request.getScheduleId(), fromStopNumber);
        for (Ticket ticket : affectedTickets) {
                emailService.sendDelayNotification(
                        ticket.getBooking().getUser(),
                        ticket,
                        request.getDelayMinutes()
                );
        }
        return delay;
    }

    @Override
    public Delay update(Delay delay) {
        return delayRepository.save(delay);
    }

    @Override
    public void delete(Integer id) {
        delayRepository.deleteById(id);
    }
}
