package com.vcs.mygit.git.util;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.IOException;
import java.nio.file.Path;

public interface GitRepositoryOpener {
    default Git openGitRepository(Path repositoryPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(repositoryPath.resolve(".git").toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repository);
    }
}


