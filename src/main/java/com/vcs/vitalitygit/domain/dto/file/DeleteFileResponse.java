package com.vcs.vitalitygit.domain.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DeleteFileResponse (
        @JsonProperty("deleted_files")
        List<String> deletedFiles
) {
}
