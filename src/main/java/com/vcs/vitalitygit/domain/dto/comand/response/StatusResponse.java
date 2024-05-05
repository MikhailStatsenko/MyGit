package com.vcs.vitalitygit.domain.dto.comand.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record StatusResponse (
        @JsonProperty("unindexed")
        Set<String> unindexed,

        @JsonProperty("indexed")
        Set<String> indexed
) {
}