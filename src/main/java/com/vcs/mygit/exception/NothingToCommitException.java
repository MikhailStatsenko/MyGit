package com.vcs.mygit.exception;

public class NothingToCommitException extends RuntimeException {
    public NothingToCommitException(String message) {
        super(message);
    }

    public NothingToCommitException(String message, Throwable cause) {
        super(message, cause);
    }
}
