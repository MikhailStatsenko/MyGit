package com.vcs.vitalitygit.domain.dto.file;

import java.util.List;
import java.util.Map;

public record UploadFilesResponse (List<String> updatedFiles, Map<String, String> addedFiles) { }
