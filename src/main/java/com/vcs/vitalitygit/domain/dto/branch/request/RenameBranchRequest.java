package com.vcs.vitalitygit.domain.dto.branch.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class RenameBranchRequest extends BranchRequest {
    @JsonProperty("new_branch_name")
    @Pattern(regexp = "^[\\w.-]+$", message = "Please use valid branch name")
    protected String newBranchName;
}
