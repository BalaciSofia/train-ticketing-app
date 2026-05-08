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

    protected Station() {}

    public Station(String city) {
        this.city = city;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}