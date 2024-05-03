package com.vcs.vitalitygit.domain.dto.branch.response;

import java.util.List;

public record ListBranchesResponse (String currentBranch, List<String> branches) {}
