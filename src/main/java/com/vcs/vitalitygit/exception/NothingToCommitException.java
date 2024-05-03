package com.vcs.vitalitygit.exception;

public class NothingToCommitException extends RuntimeException {
    public NothingToCommitException(String message) {
        super(message);
    }
}
