package domain;

public class Station {
    private String city;

    public Station(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return String.format("Station '%s'", city);
    }
}
