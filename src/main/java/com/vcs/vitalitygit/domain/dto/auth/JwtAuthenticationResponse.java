package com.vcs.vitalitygit.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtAuthenticationResponse (
        @JsonProperty("token")
        String token
) {
}
