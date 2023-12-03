package com.vcs.mygit.exception;

public class NothingToCommitException extends RuntimeException {
    public NothingToCommitException(String message) {
        super(message);
    }
}
