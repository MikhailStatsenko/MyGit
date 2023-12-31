package com.vcs.mygit.git;

import com.vcs.mygit.annotation.RepositoryOwnerAccess;
import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.*;
import com.vcs.mygit.git.service.BranchService;
import com.vcs.mygit.git.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/git/branch")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;
    @RepositoryOwnerAccess
    @PostMapping("/create/{userId}/{repositoryName}")
    public ResponseEntity<CreateBranchResponse> createBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        String newBranch = branchService.createBranch(repositoryContext, branch);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new CreateBranchResponse(newBranch, currentBranch));
    }

    @RepositoryOwnerAccess
    @PostMapping("/switch/{userId}/{repositoryName}")
    public ResponseEntity<SwitchBranchResponse> switchBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        branchService.switchBranch(repositoryContext, branch);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new SwitchBranchResponse(currentBranch));
    }

    @RepositoryOwnerAccess
    @GetMapping("/list/{userId}/{repositoryName}")
    public ResponseEntity<ListBranchesResponse> listBranches(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        List<String> branches = branchService.listBranches(repositoryContext);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new ListBranchesResponse(currentBranch, branches));
    }

    @RepositoryOwnerAccess
    @DeleteMapping("/delete/{userId}/{repositoryName}")
    public ResponseEntity<DeleteBranchResponse> deleteBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        String deletedBranch = branchService.deleteBranch(repositoryContext, branch);
        return ResponseEntity.ok(new DeleteBranchResponse(deletedBranch));
    }

    @RepositoryOwnerAccess
    @PutMapping("/rename/{userId}/{repositoryName}")
    public ResponseEntity<RenameBranchResponse> renameBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch,
            @RequestParam String newBranch,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        String newBranchName = branchService.renameBranch(repositoryContext, branch, newBranch);
        return ResponseEntity.ok(new RenameBranchResponse(newBranchName));
    }

    @RepositoryOwnerAccess
    @PostMapping("/merge/{userId}/{repositoryName}")
    public ResponseEntity<CommitResponse> mergeBranches(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branchToMerge,
            @RequestParam String branchToMergeInto,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        RevCommit mergeCommitInfo = branchService.mergeBranches(repositoryContext, branchToMerge, branchToMergeInto);
        Date commitDate = mergeCommitInfo.getAuthorIdent().getWhen();
        return ResponseEntity.ok(new CommitResponse(
                mergeCommitInfo.getId().getName(),
                DateFormatter.format(commitDate),
                mergeCommitInfo.getFullMessage()
        ));
    }
}
