package com.vcs.vitalitygit.domain.dto.comand.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class CommitRequest extends RepositoryDetails {
    @JsonProperty("message")
    @NotBlank
    protected String message;
}
