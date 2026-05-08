package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String city;

    protected Station() {}  // JPA needs a no-arg constructor

    public Station(String city) {
        this.city = city;
    }

    public Integer getId() { return id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}