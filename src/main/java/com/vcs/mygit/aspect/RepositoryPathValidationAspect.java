package com.vcs.mygit.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RepositoryPathValidationAspect {
    @Before(value = "execution(* com.vcs.mygit.git.GitService.*(..)) && args(userId, repositoryName,..)",
            argNames = "userId, repositoryName")
    public void validateRepositoryPath(String userId, String repositoryName) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty or null");
        }
        if (repositoryName == null || repositoryName.isBlank()) {
            throw new IllegalArgumentException("Repository name cannot be empty or null");
        }
    }

//    @Around(value = "execution(* com.vcs.mygit.git.GitService.*(..)) && args(userId, repositoryPath,..)",
//            argNames = "joinPoint,userId,repositoryPath")
//    public Object validateAndModifyRepositoryPath(ProceedingJoinPoint joinPoint, String userId, String repositoryPath) throws Throwable {
//        Object[] args = joinPoint.getArgs();
//        args[1] = userId + "/" + repositoryPath;
//        return joinPoint.proceed(args);
//    }
}