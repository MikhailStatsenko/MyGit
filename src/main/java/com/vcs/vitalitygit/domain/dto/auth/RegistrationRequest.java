package com.vcs.vitalitygit.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record RegistrationRequest (
        @NotBlank
        @JsonProperty("username")
        String username,

        @Email
        @JsonProperty("email")
        String email,

        @Length(min=8)
        @JsonProperty("password")
        String password
) {
}
