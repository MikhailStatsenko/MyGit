package com.vcs.mygit.exception;

public class MergeFailedException extends RuntimeException {
    public MergeFailedException(String message) {
        super(message);
    }
}
