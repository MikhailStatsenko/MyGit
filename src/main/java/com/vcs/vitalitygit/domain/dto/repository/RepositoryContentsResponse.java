package com.vcs.vitalitygit.domain.dto.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record RepositoryContentsResponse(
        @JsonProperty("elements")
        Set<String> elements
) {
}
