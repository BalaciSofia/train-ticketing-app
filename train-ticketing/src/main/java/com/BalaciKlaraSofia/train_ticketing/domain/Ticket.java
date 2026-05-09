package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "departure_schedule_stop_id", nullable = false)
    private ScheduleStop departureScheduleStop;

    @ManyToOne
    @JoinColumn(name = "arrival_schedule_stop_id", nullable = false)
    private ScheduleStop arrivalScheduleStop;

    protected Ticket() {}

    public Ticket(Booking booking, ScheduleStop departureScheduleStop, ScheduleStop arrivalScheduleStop) {
        this.booking = booking;
        this.departureScheduleStop = departureScheduleStop;
        this.arrivalScheduleStop = arrivalScheduleStop;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Booking getBooking() { return booking; }
    public ScheduleStop getDepartureScheduleStop() { return departureScheduleStop; }
    public ScheduleStop getArrivalScheduleStop() { return arrivalScheduleStop; }

    public void setBooking(Booking booking) { this.booking = booking; }
    public void setDepartureScheduleStop(ScheduleStop departureScheduleStop) { this.departureScheduleStop = departureScheduleStop; }
    public void setArrivalScheduleStop(ScheduleStop arrivalScheduleStop) { this.arrivalScheduleStop = arrivalScheduleStop; }
}
