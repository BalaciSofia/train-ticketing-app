package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String trainNumber;

    @Column(nullable = false)
    private Integer numberOfSeats;

    protected Train() {}

    public Train(String trainNumber, Integer numberOfSeats) {
        this.trainNumber = trainNumber;
        this.numberOfSeats = numberOfSeats;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTrainNumber() { return trainNumber; }
    public Integer getNumberOfSeats() { return numberOfSeats; }

    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
}
