package com.vcs.vitalitygit.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vcs.vitalitygit.domain.model.User;

public class UserDto {
    @JsonProperty("id")
    long id;

    @JsonProperty("username")
    String username;

    @JsonProperty("email")
    String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
