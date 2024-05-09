package com.vcs.vitalitygit.service;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.exception.ForbiddenAccessException;
import com.vcs.vitalitygit.exception.MergeConflictException;
import com.vcs.vitalitygit.exception.MergeFailedException;
import com.vcs.vitalitygit.util.GitRepositoryUser;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService implements GitRepositoryUser {
    private final UserService userService;

    private static String getBranchNameFromRef(Ref ref) {
        String branchName = ref.getName();
        return branchName.substring(branchName.lastIndexOf('/') + 1);
    }

    public String getCurrentBranch(RepositoryDetails repoContext) throws IOException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Repository repository = git.getRepository();
            return repository.getBranch();
        }
    }

    public String createBranch(
            RepositoryDetails repoContext,
            String branchName
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can create branches");
        }

        boolean branchAlreadyExists = listBranches(repoContext).contains(branchName);
        if (branchAlreadyExists) {
            throw new IllegalArgumentException("Branch already exists");
        }

        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            Ref newBranchRef = git.branchCreate().setName(branchName).call();
            return getBranchNameFromRef(newBranchRef);
        }
    }

    public void switchBranch(
            RepositoryDetails repoContext,
            String branchName
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can switch branches");
        }

        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            git.checkout().setForced(true).setName(branchName).call();
        }
    }

    public List<String> listBranches(RepositoryDetails repoContext) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            return git.branchList().call()
                    .stream()
                    .map(BranchService::getBranchNameFromRef)
                    .toList();
        }
    }

    public String deleteBranch(
            RepositoryDetails repoContext,
            String branchName
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can delete branches");
        }

        Path repositoryPath = repoContext.getRepositoryPath();

        if (getCurrentBranch(repoContext).equals(branchName)) {
            throw new IllegalArgumentException("Branch " + branchName +
                    " is currently is currently active, so it cannot be deleted");
        }

        try (Git git = openGitRepository(repositoryPath)) {
            git.branchDelete().setBranchNames(branchName).setForce(true).call();
            return branchName;
        }
    }

    public String renameBranch(
            RepositoryDetails repoContext,
            String branchName,
            String newBranchName
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can rename branches");
        }

        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            Ref newBranchRef =  git.branchRename()
                    .setOldName(branchName)
                    .setNewName(newBranchName)
                    .call();
            return getBranchNameFromRef(newBranchRef);
        }
    }

    public RevCommit mergeBranches(
            RepositoryDetails repoContext,
            String branchToMerge,
            String branchToMergeInto
    ) throws IOException, GitAPIException {
        if (!userService.isCurrentUserOwnsRepository(repoContext)) {
            throw new ForbiddenAccessException("Only repository owner can merge branches");
        }

        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            switchBranch(repoContext, branchToMergeInto);

            MergeResult mergeResult = git.merge()
                    .include(git.getRepository().resolve(branchToMerge))
                    .call();

            var mergeStatus = mergeResult.getMergeStatus();
            if (mergeStatus.isSuccessful()) {
                return git.commit()
                        .setMessage("Merge " + branchToMerge + " into " + branchToMergeInto)
                        .call();
            } else if (mergeStatus.equals(MergeResult.MergeStatus.CONFLICTING)) {
                List<String> conflictingFiles = mergeResult.getConflicts().keySet().stream().toList();
                throw new MergeConflictException("Merge failed: conflicts detected", conflictingFiles);
            } else {
                throw new MergeFailedException("Merge failed: " + mergeResult.getMergeStatus().name());
            }
        }
    }
}
