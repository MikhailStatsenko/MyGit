package com.vcs.mygit.git.dto.response;

import java.util.List;
import java.util.Map;

public record UploadFilesResponse (List<String> rejectedFiles, Map<String, String> addedFiles) { }
