package com.vcs.vitalitygit.domain.dto.branch.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class MergeBranchRequests extends BranchRequest {
    @JsonProperty("branch_to_merge_into")
    @Pattern(regexp = "^[\\w.-]+$", message = "Please use valid branch name")
    protected String branchToMergeInto;
}
