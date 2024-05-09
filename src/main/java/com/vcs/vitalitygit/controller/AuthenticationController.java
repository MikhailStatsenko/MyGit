package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.auth.JwtAuthenticationResponse;
import com.vcs.vitalitygit.domain.dto.auth.LoginRequest;
import com.vcs.vitalitygit.domain.dto.auth.RegistrationRequest;
import com.vcs.vitalitygit.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody RegistrationRequest request)
            throws IOException {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login (@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }
}
