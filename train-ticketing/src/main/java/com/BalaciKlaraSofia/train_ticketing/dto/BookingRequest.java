package com.BalaciKlaraSofia.train_ticketing.dto;

public class BookingRequest {
    private Integer userId;
    private Integer departureScheduleStopId;
    private Integer arrivalScheduleStopId;

    public BookingRequest() {}

    public Integer getUserId() { return userId; }
    public Integer getDepartureScheduleStopId() { return departureScheduleStopId; }
    public Integer getArrivalScheduleStopId() { return arrivalScheduleStopId; }

    public void setUserId(Integer userId) { this.userId = userId; }
    public void setDepartureScheduleStopId(Integer departureScheduleStopId) { this.departureScheduleStopId = departureScheduleStopId; }
    public void setArrivalScheduleStopId(Integer arrivalScheduleStopId) { this.arrivalScheduleStopId = arrivalScheduleStopId; }
}
