package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(User user, Ticket ticket) {
        String from = ticket.getDepartureScheduleStop().getRouteStop().getStation().getCity();
        String to = ticket.getArrivalScheduleStop().getRouteStop().getStation().getCity();
        String departure = ticket.getDepartureScheduleStop().getDepartureTime().toString();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Booking Confirmation");
        message.setText(
                "Hello " + user.getUsername() + ",\n\n" +
                "Your booking has been confirmed.\n\n" +
                "From:      " + from + "\n" +
                "To:        " + to + "\n" +
                "Departure: " + departure + "\n\n" +
                "Thank you for travelling with us!"
        );

        mailSender.send(message);
    }

    public void sendDelayNotification(User user, Ticket ticket, Integer delayMinutes) {
        String from = ticket.getDepartureScheduleStop().getRouteStop().getStation().getCity();
        String to = ticket.getArrivalScheduleStop().getRouteStop().getStation().getCity();
        String departure = ticket.getDepartureScheduleStop().getDepartureTime().toString();

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
