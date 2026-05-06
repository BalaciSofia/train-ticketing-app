package domain;

import java.util.Map;

public class Schedule {
    private Train train;
    private Route route;
    private Map<Station, DateTime> stationTimesArrival;
    private Map<Station, DateTime> stationTimesDeparture;

    public Schedule(Train train, Route route, Map<Station, DateTime> stationTimesArrival, Map<Station, DateTime> stationTimesDeparture) {
        this.train = train;
        this.route = route;
        this.stationTimesArrival = stationTimesArrival;
        this.stationTimesDeparture = stationTimesDeparture;
    }

    public Train getTrain() {
        return train;
    }

    public Route getRoute() {
        return route;
    }

    public Map<Station, DateTime> getStationTimesArrival() {
        return stationTimesArrival;
    }

    public Map<Station, DateTime> getStationTimesDeparture() {
        return stationTimesDeparture;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setStationTimesArrival(Map<Station, DateTime> stationTimesArrival) {
        this.stationTimesArrival = stationTimesArrival;
    }

    public void setStationTimesDeparture(Map<Station, DateTime> stationTimesDeparture) {
        this.stationTimesDeparture = stationTimesDeparture;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Schedule{train=%s, route=%s, stationTimesArrival={", train.getId(), route.getId()));
        for (Map.Entry<Station, DateTime> entry : stationTimesArrival.entrySet()) {
            sb.append(String.format("%s: %s, ", entry.getKey().getCity(), entry.getValue().toString()));
        }
        sb.append("}, stationTimesDeparture={");
        for (Map.Entry<Station, DateTime> entry : stationTimesDeparture.entrySet()) {
            sb.append(String.format("%s: %s, ", entry.getKey().getCity(), entry.getValue().toString()));
        }
        sb.append("}}");
        return sb.toString();
    }
}
