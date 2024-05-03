package com.vcs.vitalitygit.git.dto.response;

import java.util.List;
import java.util.Map;

public record UploadFilesResponse (List<String> updatedFiles, Map<String, String> addedFiles) { }
