package com.vcs.mygit.git.dto;

import java.nio.file.Path;

public record RepositoryContext(String userId, String repositoryName) {
    public Path getRepositoryPath() {
        return Path.of(userId + "/" + repositoryName);
    }
}
