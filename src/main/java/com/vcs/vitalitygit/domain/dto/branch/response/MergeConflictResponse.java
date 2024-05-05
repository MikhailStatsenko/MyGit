package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MergeConflictResponse (
        @JsonProperty("conflicting_files")
        List<String> conflictingFiles
) {
}
