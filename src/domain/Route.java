package domain;

import java.util.List;

public class Route {

    private List<Station> stations;

     public Route(List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    @Override
    public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("Route: ");
         for (Station station : stations) {
             sb.append(station.toString()).append(", ");
         }
         return  sb.toString();
    }
}
