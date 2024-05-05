package com.vcs.vitalitygit.domain.dto.comand.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class LogRequest extends RepositoryDetails {
    @JsonProperty("branch")
    @NotBlank
    private String branch;
}
