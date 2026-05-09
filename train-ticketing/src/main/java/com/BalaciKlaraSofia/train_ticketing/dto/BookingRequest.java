package com.BalaciKlaraSofia.train_ticketing.dto;

import java.util.List;

public class BookingRequest {
    private Integer userId;
    private List<TicketRequest> tickets;

    public BookingRequest() {}

    public Integer getUserId() { return userId; }
    public List<TicketRequest> getTickets() { return tickets; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setTickets(List<TicketRequest> tickets) { this.tickets = tickets; }
}
