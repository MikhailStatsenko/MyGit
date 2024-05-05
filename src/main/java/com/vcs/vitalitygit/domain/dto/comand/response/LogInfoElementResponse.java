package com.vcs.vitalitygit.domain.dto.comand.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LogInfoElementResponse(
        @JsonProperty("hash")
        String hash,

        @JsonProperty("date")
        String date,

        @JsonProperty("message")
        String message
) {
}
