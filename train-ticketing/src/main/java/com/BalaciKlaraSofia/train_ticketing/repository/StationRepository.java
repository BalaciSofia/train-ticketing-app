package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Integer> {
    Optional<Station> findByCity(String city);
}