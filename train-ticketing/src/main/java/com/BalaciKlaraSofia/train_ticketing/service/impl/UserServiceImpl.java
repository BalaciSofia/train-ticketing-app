package com.BalaciKlaraSofia.train_ticketing.service.impl;

import com.BalaciKlaraSofia.train_ticketing.domain.User;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginRequest;
import com.BalaciKlaraSofia.train_ticketing.repository.UserRepository;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User add(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()));
    }
}
