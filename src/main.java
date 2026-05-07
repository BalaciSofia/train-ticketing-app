import domain.Station;
import exceptions.RepositoryException;
import repository.StationRepository;

import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        StationRepository stationRepository = new StationRepository("src/stations.txt");
        ArrayList<Station> stations = stationRepository.getAllStations();
        for (Station station : stations) {
            System.out.println(station);
        }
        Station newStation = new Station("New City");
        stationRepository.addStation(newStation);
        System.out.println("After adding a new station:");
        stations = stationRepository.getAllStations();
        for (Station station : stations) {
            System.out.println(station);
        }

        Station foundStation = stationRepository.findStation("New City");
        System.out.println("Found station: " + foundStation);

        stationRepository.removeStation(newStation);

        try {
            foundStation = stationRepository.findStation("New City");
            System.out.println("Found station after removal: " + foundStation);
        } catch (RepositoryException e) {
            System.out.println(e.getMessage());
        }

    }
}
