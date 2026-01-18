package com.projekt.wypozyczalnia.controllers;

import com.projekt.wypozyczalnia.dto.auth.LoginRequestDto;
import com.projekt.wypozyczalnia.dto.auth.LogoutRequestDto;
import com.projekt.wypozyczalnia.dto.auth.RegisterRequestDto;
import com.projekt.wypozyczalnia.dto.auth.RegisterResponseDto;
import com.projekt.wypozyczalnia.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/auth", produces = { MediaType.APPLICATION_JSON_VALUE })
public class AuthController {

    private final AuthService authService;

    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        RegisterResponseDto result = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto request) {
        Object result = authService.login(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/refresh", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> refresh(@RequestBody Map<String, Object> request) {
        Object result = authService.refresh(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping(path = "/logout", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequestDto request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }
}
