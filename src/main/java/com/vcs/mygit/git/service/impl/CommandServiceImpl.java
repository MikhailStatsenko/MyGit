package com.vcs.mygit.git.service.impl;

import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.service.CommandService;
import com.vcs.mygit.git.service.GitRepositoryOpener;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Service
public class CommandServiceImpl implements CommandService, GitRepositoryOpener {
    public void init(RepositoryContext repoContext) throws GitAPIException, IOException {
        Path repositoryPath = repoContext.getRepositoryPath();
        if (Files.isDirectory(repositoryPath)) {
            throw new IllegalArgumentException("Repository already exists");
        }
        try (Git ignored = Git.init()
                .setDirectory(Files.createDirectories(repositoryPath).toFile())
                .call()
        ) {}
    }

    // TODO: doesn't work on deleted files
    public RevCommit commit(
            RepositoryContext repoContext,
            String message
    ) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Status status = git.status().call();
            if (status.getAdded().isEmpty() && status.getChanged().isEmpty()) {
                throw new NothingToCommitException("Nothing to commit, working directory clean");
            }
            return git.commit().setMessage(message).call();
        }
    }

    public Set<String> add(
            RepositoryContext repoContext,
            String filePath
    ) throws IOException, GitAPIException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("File path parameter must be present");

        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            git.add().addFilepattern(filePath).call();

            Status status = git.status().call();
            Set<String> addedFiles = new HashSet<>(status.getAdded());
            addedFiles.addAll(status.getChanged());

            return addedFiles;
        }
    }

    public Set<String> addAll(RepositoryContext repoContext) throws IOException, GitAPIException {
        return add(repoContext, ".");
    }
}
