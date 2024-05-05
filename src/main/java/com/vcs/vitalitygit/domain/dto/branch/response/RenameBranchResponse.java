package com.vcs.vitalitygit.domain.dto.branch.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RenameBranchResponse (
        @JsonProperty("new_branch_name")
        String newBranchName
) {
}
