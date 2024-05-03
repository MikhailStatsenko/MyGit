package com.vcs.vitalitygit.security;


import com.vcs.vitalitygit.security.dto.request.LoginRequest;
import com.vcs.vitalitygit.security.dto.request.RegistrationRequest;
import com.vcs.vitalitygit.security.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse register(RegistrationRequest request);

    JwtAuthenticationResponse login(LoginRequest request);
}
