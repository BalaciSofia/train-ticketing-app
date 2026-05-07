package domain;

public class Ticket {
    private int id;
    private Train train;
    private String name;
    private Station departureStation;
    private Station arrivalStation;

    public Ticket(int id, Train train, String name, Station departureStation, Station arrivalStation) {
        this.id = id;
        this.train = train;
        this.name = name;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
    }

    public int getId() {
        return id;
    }

    public Train getTrain() {
        return train;
    }

    public String getName() {
        return name;
    }

    public Station getDepartureStation() {
        return departureStation;
    }

    public Station getArrivalStation() {
        return arrivalStation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartureStation(Station departureStation) {
        this.departureStation = departureStation;
    }

    public void setArrivalStation(Station arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    @Override
    public String toString() {
        return String.format("Ticket{id=%d, train=%s, name='%s', departureStation=%s, arrivalStation=%s}", id, train.getId(), name, departureStation.getCity(), arrivalStation.getCity());
    }
}

