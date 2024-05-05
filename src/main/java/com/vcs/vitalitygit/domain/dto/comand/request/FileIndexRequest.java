package com.vcs.vitalitygit.domain.dto.comand.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class FileIndexRequest extends RepositoryDetails {
    @JsonProperty("pattern")
    @NotBlank
    private String pattern;
}
