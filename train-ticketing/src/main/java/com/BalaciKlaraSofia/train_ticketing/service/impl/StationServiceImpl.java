package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Station;
import com.BalaciKlaraSofia.train_ticketing.repository.StationRepository;
import com.BalaciKlaraSofia.train_ticketing.service.StationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StationServiceImpl implements StationService {

    private final StationRepository stationRepository;

    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    @Override
    public Optional<Station> getById(Integer id) {
        return stationRepository.findById(id);
    }

    @Override
    public Station add(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public Station update(Station station) {
        return stationRepository.save(station);
    }

    @Override
    public void delete(Integer id) {
        stationRepository.deleteById(id);
    }
}
