package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Train;

import java.util.List;
import java.util.Optional;

public interface TrainService {
    List<Train> getAll();
    Optional<Train> getById(Integer id);
    Train add(Train train);
    Train update(Train train);
    void delete(Integer id);
}
