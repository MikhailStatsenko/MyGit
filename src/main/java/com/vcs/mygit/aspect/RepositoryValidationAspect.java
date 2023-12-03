package com.vcs.mygit.aspect;

import com.vcs.mygit.exception.RepositoryNotFoundException;
import com.vcs.mygit.git.dto.RepositoryContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Aspect
@Component
public class RepositoryValidationAspect {
    @Before(value = "execution(* com.vcs.mygit.git.service.*.*(..)) && args(repositoryContext,..)",
            argNames = "repositoryContext")
    public void validateRepositoryPath(RepositoryContext repositoryContext) {
        String userId = repositoryContext.userId();
        String repositoryName = repositoryContext.repositoryName();
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty or null");
        }
        if (repositoryName == null || repositoryName.isBlank()) {
            throw new IllegalArgumentException("Repository name cannot be empty or null");
        }
    }

    @Before(value = "execution(* com.vcs.mygit.git.service.*.*(..)) && args(repositoryContext,..) " +
            "&& !execution(* *.service.*.init(..))",
            argNames = "repositoryContext")
    public void checkIfRepositoryExits(RepositoryContext repositoryContext) {
        var path = repositoryContext.getRepositoryPath();
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RepositoryNotFoundException("Repository does not exist");
        }
    }
}