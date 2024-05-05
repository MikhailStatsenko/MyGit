package com.vcs.vitalitygit.domain.dto.branch.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class BranchRequest extends RepositoryDetails {
    @JsonProperty("branch")
    @Pattern(regexp = "^[\\w.-]+$", message = "Please use valid branch name")
    protected String branch;
}
