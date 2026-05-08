package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.Delay;
import com.BalaciKlaraSofia.train_ticketing.repository.DelayRepository;
import com.BalaciKlaraSofia.train_ticketing.service.DelayService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DelayServiceImpl implements DelayService {

    private final DelayRepository delayRepository;

    public DelayServiceImpl(DelayRepository delayRepository) {
        this.delayRepository = delayRepository;
    }

    @Override
    public List<Delay> getAll() {
        return delayRepository.findAll();
    }

    @Override
    public Optional<Delay> getById(Integer id) {
        return delayRepository.findById(id);
    }

    @Override
    public Delay add(Delay delay) {
        return delayRepository.save(delay);
    }

    @Override
    public Delay update(Delay delay) {
        return delayRepository.save(delay);
    }

    @Override
    public void delete(Integer id) {
        delayRepository.deleteById(id);
    }
}
