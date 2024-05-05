package com.vcs.vitalitygit.domain.dto.file;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record UploadFilesResponse (
        @JsonProperty("updated_files")
        List<String> updatedFiles,

        @JsonProperty("added_files")
        Map<String, String> addedFiles
) {
}
