package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListBranchesResponse (
        @JsonProperty("current_branch")
        String currentBranch,

        @JsonProperty("branches")
        List<String> branches
) {
}
