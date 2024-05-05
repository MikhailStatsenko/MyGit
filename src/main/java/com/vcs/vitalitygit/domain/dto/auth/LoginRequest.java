package com.vcs.vitalitygit.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank
        @JsonProperty("username")
        String username,

        @NotBlank
        @JsonProperty("password")
        String password
) {
}
