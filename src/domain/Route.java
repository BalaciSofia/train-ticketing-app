package domain;

import java.util.List;

public class Route {
    private int id;
    private List<Station> stations;

     public Route(int id, List<Station> stations) {
        this.id = id;
        this.stations = stations;
    }

    public int getId() {
        return id;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(String.format("Route{id=%d, stations=[", id));
         for (Station station : stations) {
             sb.append(station.toString()).append(", ");
         }
         return  sb.toString();
    }
}
