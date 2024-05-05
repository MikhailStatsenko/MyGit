package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateBranchResponse (
        @JsonProperty("new_branch")
        String newBranch,

        @JsonProperty("current_branch")
        String currentBranch
) {
}
