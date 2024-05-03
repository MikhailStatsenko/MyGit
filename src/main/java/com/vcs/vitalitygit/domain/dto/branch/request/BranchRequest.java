package com.vcs.vitalitygit.domain.dto.branch.request;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class BranchRequest extends RepositoryDetails {
    @Pattern(regexp = "^[\\w.-]+$", message = "Please use valid branch name")
    protected String branch;
}
