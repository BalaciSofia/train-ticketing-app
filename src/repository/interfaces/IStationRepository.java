package repository.interfaces;

import domain.Station;

public interface IStationRepository {
     void addStation(Station station);
     void removeStation(Station station);
     Station findStation(String city);
}
