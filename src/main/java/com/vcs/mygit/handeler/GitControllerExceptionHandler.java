package com.vcs.mygit.handeler;

import com.vcs.mygit.exception.MergeConflictException;
import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.exception.RepositoryNotFoundException;
import com.vcs.mygit.git.dto.response.MergeConflictResponse;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(MergeConflictException.class)
    public ResponseEntity<MergeConflictResponse> handleMergeConflictException(MergeConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MergeConflictResponse(e.getConflictingFiles()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        return ResponseEntity.internalServerError().body("Internal Server Error: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGeneralRuntimeException(Exception e) {
        return ResponseEntity.internalServerError().body("Runtime exception: " + e.getMessage());
    }
}
