package com.vcs.vitalitygit.aspect;

import com.vcs.vitalitygit.exception.RepositoryNotFoundException;
import com.vcs.vitalitygit.git.dto.RepositoryContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Aspect
@Component
public class RepositoryValidationAspect {
    @Before(value = "execution(* com.vcs.vitalitygit.git.service.impl.*.*(..)) && args(repositoryContext,..)",
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

    @Before(value = "execution(* com.vcs.vitalitygit.git.service.impl.*.*(..)) && args(repositoryContext,..) " +
            "&& !execution(* com.vcs.vitalitygit.git.service.impl.*.init(..))",
            argNames = "repositoryContext")
    public void checkIfRepositoryExits(RepositoryContext repositoryContext) {
        var path = repositoryContext.getRepositoryPath();
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RepositoryNotFoundException("Repository does not exist");
        }
    }
}