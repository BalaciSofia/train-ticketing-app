package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    protected Booking() {}

    public Booking(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }
}
