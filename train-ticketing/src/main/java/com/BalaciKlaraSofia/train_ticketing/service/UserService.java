package com.BalaciKlaraSofia.train_ticketing.service;

import com.BalaciKlaraSofia.train_ticketing.domain.User;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginRequest;
import com.BalaciKlaraSofia.train_ticketing.dto.RegisterRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();
    Optional<User> getById(Integer id);
    User add(User user);
    User update(User user);
    void delete(Integer id);
    Optional<User> login(LoginRequest request);
    User register(RegisterRequest request);
}
