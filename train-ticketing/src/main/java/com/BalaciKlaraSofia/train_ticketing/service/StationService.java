package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Station;

import java.util.List;
import java.util.Optional;

public interface StationService {
    List<Station> getAll();
    Optional<Station> getById(Integer id);
    Station add(Station station);
    Station update(Station station);
    void delete(Integer id);
}
