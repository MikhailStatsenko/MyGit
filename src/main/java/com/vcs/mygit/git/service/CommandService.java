package com.vcs.mygit.git.service;

import com.vcs.mygit.aspect.RepositoryValidationAspect;
import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.git.dto.RepositoryContext;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.IOException;
import java.util.Set;


public interface CommandService {

    void init(RepositoryContext repoContext) throws GitAPIException, IOException;

    RevCommit commit(RepositoryContext repoContext, String message)
            throws IOException, GitAPIException, NothingToCommitException;

    Set<String> add(RepositoryContext repoContext, String filePath)
            throws IOException, GitAPIException;

    Set<String> addAll(RepositoryContext repoContext)
            throws IOException, GitAPIException;
}
