package com.BalaciKlaraSofia.train_ticketing.dto;

public class DelayRequest {
    private Integer scheduleId;
    private Integer fromScheduleStopId;
    private Integer delayMinutes;

    public DelayRequest() {}

    public Integer getScheduleId() { return scheduleId; }
    public Integer getFromScheduleStopId() { return fromScheduleStopId; }
    public Integer getDelayMinutes() { return delayMinutes; }

    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public void setFromScheduleStopId(Integer fromScheduleStopId) { this.fromScheduleStopId = fromScheduleStopId; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }
}
