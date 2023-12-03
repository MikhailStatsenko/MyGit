package com.vcs.mygit.git.service;

import com.vcs.mygit.git.dto.RepositoryContext;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;

public interface BranchService {
    String getCurrentBranch(RepositoryContext repoContext) throws IOException;
    String createBranch(RepositoryContext repoContext, String branchName) throws IOException, GitAPIException;
    void switchBranch(RepositoryContext repoContext, String branchName) throws IOException, GitAPIException;
    List<String> listBranches(RepositoryContext repoContext) throws IOException, GitAPIException;
    String deleteBranch(RepositoryContext repoContext, String branchName) throws IOException, GitAPIException;
    String  renameBranch(RepositoryContext repoContext, String branchName, String newBranchName) throws IOException, GitAPIException;
    RevCommit mergeBranches(RepositoryContext repoContext, String branchToMerge, String branchToMergeInto) throws IOException, GitAPIException;
}
