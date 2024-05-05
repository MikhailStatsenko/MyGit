package com.vcs.vitalitygit.service;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.comand.response.LogInfoElementResponse;
import com.vcs.vitalitygit.domain.dto.comand.response.StatusResponse;
import com.vcs.vitalitygit.exception.ForbiddenAccessException;
import com.vcs.vitalitygit.exception.NothingToCommitException;
import com.vcs.vitalitygit.util.DateFormatter;
import com.vcs.vitalitygit.util.GitRepositoryOpener;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CommandService implements GitRepositoryOpener {
    private final UserService userService;

    private void createInitialCommit(RepositoryDetails repoContext) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            git.commit()
                    .setMessage("Repository " + repoContext.getRepositoryName() + " created")
                    .setAllowEmpty(true)
                    .call();
        }
    }

    public void init(RepositoryDetails repoContext) throws GitAPIException, IOException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("You can't create repositories for other users");
        }

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

    public RevCommit commit(
            RepositoryDetails repoContext,
            String message
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can make commits");
        }

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

    public Set<String> add(
            RepositoryDetails repoContext,
            String filePath
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can add files to index");
        }

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

    public Set<String> addAll(RepositoryDetails repoContext) throws IOException, GitAPIException {
        return add(repoContext, ".");
    }

    public Set<String> remove(RepositoryDetails repoContext, String filePath) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can remove files from index");
        }

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

    public List<LogInfoElementResponse> log(RepositoryDetails repoContext, String branch) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Iterable<RevCommit> logs = git.log().add(git.getRepository().resolve(branch)).call();
            List<LogInfoElementResponse> commitLog = new ArrayList<>();

            for (RevCommit commit : logs) {
                Date commitDate = commit.getAuthorIdent().getWhen();
                LogInfoElementResponse commitInfo = new LogInfoElementResponse(
                        commit.getId().getName(),
                        DateFormatter.format(commitDate),
                        commit.getFullMessage()
                );
                commitLog.add(commitInfo);
            }

            return commitLog;
        }
    }

    public StatusResponse status(RepositoryDetails repoContext) throws IOException, GitAPIException {
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
