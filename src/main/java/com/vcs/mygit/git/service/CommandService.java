package com.vcs.mygit.git.service;

import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.git.dto.CommitInfo;
import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.StatusResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface CommandService {

    void init(RepositoryContext repoContext) throws GitAPIException, IOException;

    RevCommit commit(RepositoryContext repoContext, String message)
            throws IOException, GitAPIException, NothingToCommitException;

    Set<String> add(RepositoryContext repoContext, String filePath)
            throws IOException, GitAPIException;

    Set<String> addAll(RepositoryContext repoContext)
            throws IOException, GitAPIException;

    Set<String> remove(RepositoryContext repoContext, String filePath) throws IOException, GitAPIException;

    List<CommitInfo> log(RepositoryContext repoContext, String branch) throws IOException, GitAPIException;

    StatusResponse status(RepositoryContext repoContext) throws IOException, GitAPIException;
}
