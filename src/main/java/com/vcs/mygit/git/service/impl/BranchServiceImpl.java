package com.vcs.mygit.git.service.impl;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.service.BranchService;
import com.vcs.mygit.git.service.GitRepositoryOpener;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class BranchServiceImpl implements BranchService, GitRepositoryOpener {
    private static String getBranchNameFromRef(Ref ref) {
        String branchName = ref.getName();
        return branchName.substring(branchName.lastIndexOf('/') + 1);
    }

    public String getCurrentBranch(RepositoryContext repoContext) throws IOException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            Repository repository = git.getRepository();
            return repository.getBranch();
        }
    }
    public String createBranch(
            RepositoryContext repoContext,
            String branchName
    ) throws IOException, GitAPIException {
        boolean branchAlreadyExists = listBranches(repoContext).contains(branchName);
        if (branchAlreadyExists)
            throw new IllegalArgumentException("Branch already exists");

        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            Ref newBranchRef = git.branchCreate().setName(branchName).call();
            return getBranchNameFromRef(newBranchRef);
        }
    }

    public void switchBranch(
            RepositoryContext repoContext,
            String branchName
    ) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();
        try (Git git = openGitRepository(repositoryPath)) {
            git.checkout().setName(branchName).call();
        }
    }

    public List<String> listBranches(RepositoryContext repoContext) throws IOException, GitAPIException {
        Path repositoryPath = repoContext.getRepositoryPath();

        try (Git git = openGitRepository(repositoryPath)) {
            return git.branchList().call()
                    .stream()
                    .map(BranchServiceImpl::getBranchNameFromRef)
                    .toList();
        }
    }

    public String deleteBranch(
            RepositoryContext repoContext,
            String branchName
    ) throws IOException, GitAPIException {
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
            RepositoryContext repoContext,
            String branchName,
            String newBranchName
    ) throws IOException, GitAPIException {
        String branchPattern = "^[\\w.-]+$";
        if (!Pattern.matches(branchPattern, newBranchName)) {
            throw new IllegalArgumentException("Invalid branch name. Branch names can only contain " +
                            "letters, numbers, underscore (_) or dash (-)");
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
}
