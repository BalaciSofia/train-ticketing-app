package domain;

import java.util.List;

public class Train {
    private int id;
    private String provider;
    private int numberOfSeats;
    private List<Schedule> schedules;

    public Train(int id, String provider, int numberOfSeats, List<Schedule> schedules) {
        this.id = id;
        this.provider = provider;
        this.numberOfSeats = numberOfSeats;
        this.schedules = schedules;
    }

     public int getId() {
        return id;
    }

    public String getProvider() {
        return provider;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Train{id=%d, provider='%s', numberOfSeats=%d, schedules=[", id, provider, numberOfSeats));
        for (Schedule schedule : schedules) {
            sb.append(schedule.toString()).append(", ");
        }
        sb.append("]}");
        return sb.toString();
    }
}
