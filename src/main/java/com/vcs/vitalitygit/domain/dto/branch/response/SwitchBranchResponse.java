package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SwitchBranchResponse (
        @JsonProperty("current_branch")
        String currentBranch
) {
}
