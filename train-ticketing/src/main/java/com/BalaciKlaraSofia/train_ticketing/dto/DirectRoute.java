package com.BalaciKlaraSofia.train_ticketing.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class DirectRoute {

    private String trainNumber;
    private String fromCity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime departureTime;
    private String toCity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime arrivalTime;
    private Integer departureScheduleStopId;
    private Integer arrivalScheduleStopId;

    public DirectRoute(String trainNumber, String fromCity, LocalDateTime departureTime,
                       String toCity, LocalDateTime arrivalTime,
                       Integer departureScheduleStopId, Integer arrivalScheduleStopId) {
        this.trainNumber = trainNumber;
        this.fromCity = fromCity;
        this.departureTime = departureTime;
        this.toCity = toCity;
        this.arrivalTime = arrivalTime;
        this.departureScheduleStopId = departureScheduleStopId;
        this.arrivalScheduleStopId = arrivalScheduleStopId;
    }

    public String getTrainNumber() { return trainNumber; }
    public String getFromCity() { return fromCity; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public String getToCity() { return toCity; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public Integer getDepartureScheduleStopId() { return departureScheduleStopId; }
    public Integer getArrivalScheduleStopId() { return arrivalScheduleStopId; }
}
