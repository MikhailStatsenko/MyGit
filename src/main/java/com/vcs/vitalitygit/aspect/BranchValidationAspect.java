package com.vcs.vitalitygit.aspect;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

//@Aspect
@Component
@RequiredArgsConstructor
public class BranchValidationAspect {
    private final BranchService branchService;

    @Before(value = "within(com.vcs.vitalitygit.service.BranchService+) && " +
            "execution(* com.vcs.vitalitygit.git.service.impl.*.*(..)) && args(repositoryContext, branchName, ..) " +
            "&& !execution(* com.vcs.vitalitygit.git.service.impl.*.createBranch(..))",
            argNames = "repositoryContext, branchName")
    public void checkIfBranchExists(
            RepositoryDetails repositoryDetails,
            String branchName
    ) throws GitAPIException, IOException {
        List<String> branches = branchService.listBranches(repositoryDetails);
        boolean branchExists = branches.contains(branchName);
        if (!branchExists)
            throw new IllegalArgumentException("Branch " + branchName + " not found");
    }
}
