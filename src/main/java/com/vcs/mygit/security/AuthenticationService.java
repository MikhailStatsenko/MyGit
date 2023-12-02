package com.vcs.mygit.security;


import com.vcs.mygit.security.dto.request.LoginRequest;
import com.vcs.mygit.security.dto.request.RegistrationRequest;
import com.vcs.mygit.security.dto.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse register(RegistrationRequest request);

    JwtAuthenticationResponse login(LoginRequest request);
}
