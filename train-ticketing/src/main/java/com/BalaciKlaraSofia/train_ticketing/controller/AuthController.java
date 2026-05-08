package com.BalaciKlaraSofia.train_ticketing.controller;

import com.BalaciKlaraSofia.train_ticketing.dto.LoginRequest;
import com.BalaciKlaraSofia.train_ticketing.dto.LoginResponse;
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
        return userService.login(request)
                .map(user -> ResponseEntity.ok(new LoginResponse(user.getId(), user.getUsername(), user.getRole().name())))
                .orElse(ResponseEntity.status(401).build());
    }
}
