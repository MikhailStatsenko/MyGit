package com.vcs.mygit.exception;

import java.util.List;

public class MergeConflictException extends RuntimeException {
    private final List<String> conflictingFiles;

    public MergeConflictException(String message, List<String> conflictingFiles) {
        super(message);
        this.conflictingFiles = conflictingFiles;
    }

    public List<String> getConflictingFiles() {
        return conflictingFiles;
    }
}
