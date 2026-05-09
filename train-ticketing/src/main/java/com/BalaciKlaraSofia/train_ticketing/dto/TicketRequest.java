package com.BalaciKlaraSofia.train_ticketing.dto;

public class TicketRequest {
    private Integer departureScheduleStopId;
    private Integer arrivalScheduleStopId;

    public TicketRequest() {}

    public Integer getDepartureScheduleStopId() { return departureScheduleStopId; }
    public Integer getArrivalScheduleStopId() { return arrivalScheduleStopId; }
    public void setDepartureScheduleStopId(Integer departureScheduleStopId) { this.departureScheduleStopId = departureScheduleStopId; }
    public void setArrivalScheduleStopId(Integer arrivalScheduleStopId) { this.arrivalScheduleStopId = arrivalScheduleStopId; }
}
