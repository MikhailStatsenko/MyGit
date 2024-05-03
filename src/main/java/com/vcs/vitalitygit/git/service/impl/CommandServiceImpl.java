package com.vcs.vitalitygit.git.service.impl;

import com.vcs.vitalitygit.exception.NothingToCommitException;
import com.vcs.vitalitygit.git.dto.CommitInfo;
import com.vcs.vitalitygit.git.dto.RepositoryContext;
import com.vcs.vitalitygit.git.dto.response.StatusResponse;
import com.vcs.vitalitygit.git.service.CommandService;
import com.vcs.vitalitygit.git.util.DateFormatter;
import com.vcs.vitalitygit.git.util.GitRepositoryOpener;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class CommandServiceImpl implements CommandService, GitRepositoryOpener {
    private void createInitialCommit(RepositoryContext repoContext) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            git.commit()
                    .setMessage("Repository " + repoContext.repositoryName() + " created")
                    .setAllowEmpty(true)
                    .call();
        }
    }

    @Override
    public void init(RepositoryContext repoContext) throws GitAPIException, IOException {
        Path repositoryPath = repoContext.getRepositoryPath();
        if (Files.isDirectory(repositoryPath)) {
            throw new IllegalArgumentException("Repository already exists");
        }
        try (Git ignored = Git.init()
                .setDirectory(Files.createDirectories(repositoryPath).toFile())
                .call()
        ) {
            createInitialCommit(repoContext);
        }
    }

    @Override
    public RevCommit commit(
            RepositoryContext repoContext,
            String message
    ) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Status status = git.status().call();
            if (status.getAdded().isEmpty()
                    && status.getChanged().isEmpty()
                    && status.getRemoved().isEmpty()) {
                throw new NothingToCommitException("Nothing to commit, working directory clean");
            }
            return git.commit().setMessage(message).call();
        }
    }

    @Override
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

    @Override
    public Set<String> addAll(RepositoryContext repoContext) throws IOException, GitAPIException {
        return add(repoContext, ".");
    }

    @Override
    public Set<String> remove(RepositoryContext repoContext, String filePath) throws IOException, GitAPIException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path parameter must be present");
        }
        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            git.reset().addPath(filePath).call();

            Status status = git.status().call();

            return new HashSet<>(status.getUntracked());
        }
    }

    @Override
    public List<CommitInfo> log(RepositoryContext repoContext, String branch) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Iterable<RevCommit> logs = git.log().add(git.getRepository().resolve(branch)).call();
            List<CommitInfo> commitLog = new ArrayList<>();

            for (RevCommit commit : logs) {
                Date commitDate = commit.getAuthorIdent().getWhen();
                CommitInfo commitInfo = new CommitInfo(
                        commit.getId().getName(),
                        DateFormatter.format(commitDate),
                        commit.getFullMessage()
                );
                commitLog.add(commitInfo);
            }

            return commitLog;
        }
    }

    @Override
    public StatusResponse status(RepositoryContext repoContext) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Status status = git.status().call();

            Set<String> unindexed = new HashSet<>(status.getUntracked());
            unindexed.addAll(status.getModified());

            Set<String> indexed = new HashSet<>(status.getMissing());
            indexed.addAll(status.getChanged());
            indexed.addAll(status.getRemoved());
            indexed.addAll(status.getAdded());

            return new StatusResponse(unindexed, indexed);
        }
    }
}
