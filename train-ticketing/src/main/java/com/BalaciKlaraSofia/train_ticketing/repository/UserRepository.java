package com.BalaciKlaraSofia.train_ticketing.repository;

import com.BalaciKlaraSofia.train_ticketing.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
