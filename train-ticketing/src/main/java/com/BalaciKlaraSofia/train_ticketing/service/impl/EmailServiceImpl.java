package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.domain.User;
import com.BalaciKlaraSofia.train_ticketing.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    private static final DateTimeFormatter DATE_FORMAT = ofPattern("yyyy-MM-dd | HH:mm");

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(User user, List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello ").append(user.getUsername()).append(",\n\n");
        sb.append("Your booking has been confirmed.\n\n");
        sb.append("Tickets (").append(tickets.size()).append("):\n");

        for (int i = 0; i < tickets.size(); i++) {
            Ticket t = tickets.get(i);
            String from = t.getDepartureScheduleStop().getRouteStop().getStation().getCity();
            String to   = t.getArrivalScheduleStop().getRouteStop().getStation().getCity();
            String dep  = t.getDepartureScheduleStop().getDepartureTime().format(DATE_FORMAT);
            sb.append("  ").append(i + 1).append(". ")
              .append(from).append(" → ").append(to)
              .append(" | ").append(dep).append("\n");
        }

        sb.append("\nThank you for travelling with us!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Booking Confirmation");
        message.setText(sb.toString());

        mailSender.send(message);
    }

    public void sendDelayNotification(User user, Ticket ticket, Integer delayMinutes) {
        String from      = ticket.getDepartureScheduleStop().getRouteStop().getStation().getCity();
        String to        = ticket.getArrivalScheduleStop().getRouteStop().getStation().getCity();
        String departure = ticket.getDepartureScheduleStop().getDepartureTime().format(DATE_FORMAT);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Train Delay Notification");
        message.setText(
                "Hello " + user.getUsername() + ",\n\n" +
                "We regret to inform you that your train has been delayed by " + delayMinutes + " minutes.\n\n" +
                "From:               " + from + "\n" +
                "To:                 " + to + "\n" +
                "Original departure: " + departure + "\n\n" +
                "We apologise for the inconvenience."
        );

        mailSender.send(message);
    }
}
