package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeleteBranchResponse (
        @JsonProperty("deleted_branch")
        String deletedBranch
) {
}
