package com.vcs.vitalitygit.handeler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.vcs.mygit.security")
public class SecurityExceptionHandler {
    @ExceptionHandler({
            UsernameNotFoundException.class,
            IllegalArgumentException.class,
    })
    public ResponseEntity<String> handleRepositoryExceptions(RuntimeException e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
}