package com.vcs.vitalitygit.aspect;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.exception.RepositoryNotFoundException;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

//@Aspect
@Component
public class RepositoryValidationAspect {
    @Before(value = "execution(* com.vcs.vitalitygit.git.service.impl.*.*(..)) && args(repositoryContext,..) " +
            "&& !execution(* com.vcs.vitalitygit.git.service.impl.*.init(..))",
            argNames = "repositoryContext")
    public void checkIfRepositoryExits(RepositoryDetails repositoryDetails) {
        var path = repositoryDetails.getRepositoryPath();
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RepositoryNotFoundException("Repository does not exist");
        }
    }
}