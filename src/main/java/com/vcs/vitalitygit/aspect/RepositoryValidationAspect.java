package com.vcs.vitalitygit.aspect;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.exception.RepositoryNotFoundException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.nio.file.Files;

@Aspect
@Component
public class RepositoryValidationAspect {

    @Before("""
            execution(* com.vcs.vitalitygit.service..*(..)) \
            && !execution(* com.vcs.vitalitygit.service.UserService.*(..)) \
            && !execution(* com.vcs.vitalitygit.service.CommandService.init(..)) \
            && args(repoContext)""")
    public void checkIfRepositoryExits(RepositoryDetails repoContext) throws RepositoryNotFoundException {
        var path = repoContext.getRepositoryPath();
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RepositoryNotFoundException("There is no such repository: " + repoContext.getRepositoryName());
        }
    }
}