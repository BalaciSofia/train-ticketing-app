package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.dto.LoginRequest;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginResponse;
import com.BalaciKlaraSofia.train_ticketing.dto.RegisterRequest;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return userService.login(request)
                .map(user -> ResponseEntity.ok(new LoginResponse(user.getId(), user.getUsername(), user.getRole().name())))
                .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }
        try {
            userService.register(request);
            return ResponseEntity.status(201).body("Account created.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Username or email is already in use.");
        }
    }
}
