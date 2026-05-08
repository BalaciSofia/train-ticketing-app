package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer bookingId;

    @Column(nullable = false)
    private Integer departureScheduleStopId;

    @Column(nullable = false)
    private Integer arrivalScheduleStopId;

    protected Ticket() {}

    public Ticket(Integer bookingId, Integer departureScheduleStopId, Integer arrivalScheduleStopId) {
        this.bookingId = bookingId;
        this.departureScheduleStopId = departureScheduleStopId;
        this.arrivalScheduleStopId = arrivalScheduleStopId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getBookingId() { return bookingId; }
    public Integer getDepartureScheduleStopId() { return departureScheduleStopId; }
    public Integer getArrivalScheduleStopId() { return arrivalScheduleStopId; }

    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }
    public void setDepartureScheduleStopId(Integer departureScheduleStopId) { this.departureScheduleStopId = departureScheduleStopId; }
    public void setArrivalScheduleStopId(Integer arrivalScheduleStopId) { this.arrivalScheduleStopId = arrivalScheduleStopId; }
}
