package com.vcs.mygit.git.dto;

import java.nio.file.Path;

public record RepositoryContext(String userId, String repositoryName) {
    private static final String rootPath = "repositories";

    public static String rootPath() {
        return rootPath;
    }

    public Path getRepositoryPath() {
        return Path.of(rootPath + "/" + userId + "/" + repositoryName);
    }
}
