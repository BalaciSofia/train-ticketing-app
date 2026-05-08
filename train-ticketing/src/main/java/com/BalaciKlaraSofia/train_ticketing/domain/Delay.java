package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "delays")
public class Delay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer scheduleId;

    @Column(nullable = false)
    private Integer delayMinutes;

    protected Delay() {}

    public Delay(Integer scheduleId, Integer delayMinutes) {
        this.scheduleId = scheduleId;
        this.delayMinutes = delayMinutes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getScheduleId() { return scheduleId; }
    public Integer getDelayMinutes() { return delayMinutes; }

    public void setScheduleId(Integer scheduleId) { this.scheduleId = scheduleId; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }
}
