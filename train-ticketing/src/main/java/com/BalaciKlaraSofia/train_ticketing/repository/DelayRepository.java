package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.Delay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DelayRepository extends JpaRepository<Delay, Integer> {
}
