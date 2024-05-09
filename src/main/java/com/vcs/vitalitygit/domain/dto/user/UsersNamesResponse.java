package com.vcs.vitalitygit.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UsersNamesResponse(
        @JsonProperty("users")
        List<UserDto> users
) {
}
