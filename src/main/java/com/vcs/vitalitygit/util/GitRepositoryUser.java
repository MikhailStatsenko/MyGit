package com.vcs.vitalitygit.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Path;

public interface GitRepositoryUser {
    default Git openGitRepository(Path repositoryPath) throws IOException {
        return new Git(getClosedRepository(repositoryPath));
    }

    default Repository getClosedRepository(Path repositoryPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder.setGitDir(repositoryPath.resolve(".git").toFile())
                .readEnvironment()
                .findGitDir()
                .build();
    }
}


