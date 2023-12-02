package com.vcs.mygit.handeler;

import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.exception.RepositoryNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = "com.vcs.mygit.git")
public class GitControllerExceptionHandler {
    @ExceptionHandler({
            RepositoryNotFoundException.class,
            NothingToCommitException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<String> handleRepositoryExceptions(RuntimeException e) {
        return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
    }
}
