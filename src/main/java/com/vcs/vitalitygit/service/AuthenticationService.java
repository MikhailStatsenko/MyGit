package com.vcs.vitalitygit.service;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.auth.JwtAuthenticationResponse;
import com.vcs.vitalitygit.domain.dto.auth.LoginRequest;
import com.vcs.vitalitygit.domain.dto.auth.RegistrationRequest;
import com.vcs.vitalitygit.domain.model.User;
import com.vcs.vitalitygit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationResponse register(RegistrationRequest request) throws IOException {
        if (userRepository.findUserByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);

        Path userReposPath = Path.of(RepositoryDetails.rootPath()).resolve(request.username());
        if (Files.notExists(userReposPath)) {
            Files.createDirectories(userReposPath);
        }

        return new JwtAuthenticationResponse(jwt);
    }

    public JwtAuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userRepository.findUserByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("User with this email not found"));

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
