package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.domain.User;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginRequest;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginResponse;
import com.BalaciKlaraSofia.train_ticketing.dto.RegisterRequest;
import com.BalaciKlaraSofia.train_ticketing.service.UserService;
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
        User user = userService.login(request);
        return ResponseEntity.ok(new LoginResponse(user.getId(), user.getUsername(), user.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("All fields are required.");
        }
        userService.register(request);
        return ResponseEntity.status(201).body("Account created.");
    }
}
