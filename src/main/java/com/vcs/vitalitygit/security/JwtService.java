package com.vcs.vitalitygit.security;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

public interface JwtService {
    String extractUserName(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    String extractTokenFromRequest(HttpServletRequest request);
}
