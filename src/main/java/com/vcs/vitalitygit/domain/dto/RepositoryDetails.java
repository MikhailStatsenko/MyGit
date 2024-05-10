package com.vcs.vitalitygit.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.nio.file.Path;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDetails {

    private static final String rootPath = "repositories";

    @NotBlank(message = "Fill in username")
    protected String username;

    @JsonProperty("repository_name")
    @NotBlank(message = "Fill in the repository name")
    protected String repositoryName;

    public static String rootPath() {
        return rootPath;
    }

    public Path getRepositoryPath() {
        return Path.of(rootPath + "/" + username + "/" +
                repositoryName);
    }
}


