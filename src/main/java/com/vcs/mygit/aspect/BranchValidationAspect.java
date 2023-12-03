package com.vcs.mygit.aspect;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
public class BranchValidationAspect {
    private final BranchService branchService;

    @Before(value = "execution(* com.vcs.mygit.git.service.impl.*.*(..)) && args(branchName, ..)", argNames = "branchName")
    public void validateBranchName(String branchName) {
        String branchPattern = "^[\\w.-]+$";
        if (!Pattern.matches(branchPattern, branchName)) {
            throw new IllegalArgumentException("Invalid branch name. Branch names can only contain " +
                            "letters, numbers, underscore (_) or dash (-)");
        }
    }

    @Before(value = "within(com.vcs.mygit.git.service.BranchService+) && " +
            "execution(* com.vcs.mygit.git.service.impl.*.*(..)) && args(repositoryContext, branchName, ..) " +
            "&& !execution(* com.vcs.mygit.git.service.impl.*.createBranch(..))",
            argNames = "repositoryContext, branchName")
    public void checkIfBranchExists(
            RepositoryContext repositoryContext,
            String branchName
    ) throws GitAPIException, IOException {
        List<String> branches = branchService.listBranches(repositoryContext);
        boolean branchExists = branches.contains(branchName);
        if (!branchExists)
            throw new IllegalArgumentException("Branch " + branchName + " not found");
    }
}
