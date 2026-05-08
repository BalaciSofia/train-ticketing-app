package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Train;
import com.BalaciKlaraSofia.train_ticketing.repository.TrainRepository;
import com.BalaciKlaraSofia.train_ticketing.service.TrainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainServiceImpl implements TrainService {

    private final TrainRepository trainRepository;

    public TrainServiceImpl(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @Override
    public List<Train> getAll() {
        return trainRepository.findAll();
    }

    @Override
    public Optional<Train> getById(Integer id) {
        return trainRepository.findById(id);
    }

    @Override
    public Train add(Train train) {
        return trainRepository.save(train);
    }

    @Override
    public Train update(Train train) {
        return trainRepository.save(train);
    }

    @Override
    public void delete(Integer id) {
        trainRepository.deleteById(id);
    }
}
