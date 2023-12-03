package com.vcs.mygit.git.dto.response;

import java.util.List;

public record ListBranchesResponse (String currentBranch, List<String> branches) {}
