package com.vcs.mygit.security;

import com.vcs.mygit.security.dto.request.LoginRequest;
import com.vcs.mygit.security.dto.request.RegistrationRequest;
import com.vcs.mygit.security.dto.response.JwtAuthenticationResponse;
//import com.vcs.mygit.user.Role;
import com.vcs.mygit.user.User;
import com.vcs.mygit.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public JwtAuthenticationResponse register(RegistrationRequest request) {
        if (userRepository.findUserByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        var user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
//                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public JwtAuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        var user = userRepository.findUserByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password"));

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
