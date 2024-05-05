package com.vcs.vitalitygit.aspect;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class BranchValidationAspect {
    private final BranchService branchService;

    @Before(value = """
            within(com.vcs.vitalitygit.service.BranchService) \
            && execution(* com.vcs.vitalitygit.service.*.*(..))\
            && !execution(* com.vcs.vitalitygit.service.*.createBranch(..))
            && args(repoContext, branchName, ..)""",
            argNames = "repoContext, branchName")
    public void checkIfBranchExists(RepositoryDetails repoContext, String branchName
    ) throws GitAPIException, IOException {
        List<String> branches = branchService.listBranches(repoContext);
        boolean branchExists = branches.contains(branchName);
        if (!branchExists)
            throw new IllegalArgumentException("Branch " + branchName + " not found");
    }
}
