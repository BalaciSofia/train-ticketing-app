package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Ticket;
import com.BalaciKlaraSofia.train_ticketing.domain.User;

import java.util.List;

public interface EmailService {
    void sendBookingConfirmation(User user, List<Ticket> tickets);
    void sendDelayNotification(User user, Ticket ticket, Integer delayMinutes);
}
