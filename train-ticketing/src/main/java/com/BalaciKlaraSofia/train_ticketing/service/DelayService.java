package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.Delay;
import com.BalaciKlaraSofia.train_ticketing.dto.DelayRequest;

import java.util.List;
import java.util.Optional;

public interface DelayService {
    List<Delay> getAll();
    Optional<Delay> getById(Integer id);
    Delay report(DelayRequest request);
    Delay update(Delay delay);
    void delete(Integer id);
}
