package com.vcs.vitalitygit.exception;

public class MergeFailedException extends RuntimeException {
    public MergeFailedException(String message) {
        super(message);
    }
}
