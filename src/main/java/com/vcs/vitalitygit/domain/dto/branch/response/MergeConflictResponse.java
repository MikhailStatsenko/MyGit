package com.vcs.vitalitygit.domain.dto.branch.response;

import java.util.List;

public record MergeConflictResponse (List<String> conflictingFiles) {}
