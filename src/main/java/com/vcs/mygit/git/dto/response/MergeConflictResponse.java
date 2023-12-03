package com.vcs.mygit.git.dto.response;

import java.util.List;

public record MergeConflictResponse (List<String> conflictingFiles) {}
