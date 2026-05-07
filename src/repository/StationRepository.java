package repository;
import domain.Station;
import exceptions.RepositoryException;
import repository.interfaces.IStationRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationRepository implements IStationRepository {

    private ArrayList<Station> stations;
    private String filePath;

    public StationRepository(String filePath) {
        this.filePath = filePath;
        loadStations();
    }

    public ArrayList<Station> getAllStations() {
        return this.stations;
    }

    private void loadStations() {
        stations = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                Station station = new Station(line);
                stations.add(station);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveStations() {
        List<String> lines = new ArrayList<>();
        for (Station station : stations) {
            lines.add(station.getCity());
        }
        try {
            Files.write(Paths.get(filePath), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addStation(Station station) throws RepositoryException {
        if(findStation(station.getCity()) != null) {
            throw new RepositoryException("Station already exists: " + station.getCity());
        }
        stations.add(station);
        saveStations();
    }

    public void removeStation(Station station) {
        stations.remove(station);
        saveStations();
    }

    public Station findStation(String city) {
        for (Station station : stations) {
            if (Objects.equals(station.getCity(), city)) {
                return station;
            }
        }
        return null;
    }
}