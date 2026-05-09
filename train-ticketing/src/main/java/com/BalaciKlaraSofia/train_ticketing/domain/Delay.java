package com.BalaciKlaraSofia.train_ticketing.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "delays")
public class Delay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "from_schedule_stop_id", nullable = false)
    private ScheduleStop fromScheduleStop;

    @Column(nullable = false)
    private Integer delayMinutes;

    protected Delay() {}

    public Delay(Schedule schedule, ScheduleStop fromScheduleStop, Integer delayMinutes) {
        this.schedule = schedule;
        this.fromScheduleStop = fromScheduleStop;
        this.delayMinutes = delayMinutes;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Schedule getSchedule() { return schedule; }
    public ScheduleStop getFromScheduleStop() { return fromScheduleStop; }
    public Integer getDelayMinutes() { return delayMinutes; }

    public void setSchedule(Schedule schedule) { this.schedule = schedule; }
    public void setFromScheduleStop(ScheduleStop fromScheduleStop) { this.fromScheduleStop = fromScheduleStop; }
    public void setDelayMinutes(Integer delayMinutes) { this.delayMinutes = delayMinutes; }
}
