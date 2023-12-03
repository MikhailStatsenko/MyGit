package com.vcs.mygit.git;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.*;
import com.vcs.mygit.git.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/git")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;
    @PostMapping("/create_branch/{userId}/{repositoryName}")
    public ResponseEntity<CreateBranchResponse> createBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);

        String newBranch = branchService.createBranch(repositoryContext, branch);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new CreateBranchResponse(newBranch, currentBranch));
    }

    @PostMapping("/switch_branch/{userId}/{repositoryName}")
    public ResponseEntity<SwitchBranchResponse> switchBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);

        branchService.switchBranch(repositoryContext, branch);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new SwitchBranchResponse(currentBranch));
    }

    @GetMapping("/list_branches/{userId}/{repositoryName}")
    public ResponseEntity<ListBranchesResponse> listBranches(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);

        List<String> branches = branchService.listBranches(repositoryContext);
        String currentBranch = branchService.getCurrentBranch(repositoryContext);
        return ResponseEntity.ok(new ListBranchesResponse(currentBranch, branches));
    }

    @DeleteMapping("/delete_branch/{userId}/{repositoryName}")
    public ResponseEntity<DeleteBranchResponse> deleteBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch
    ) throws GitAPIException, IOException {
        String deletedBranch = branchService.deleteBranch(
                new RepositoryContext(userId, repositoryName),
                branch
        );
        return ResponseEntity.ok(new DeleteBranchResponse(deletedBranch));
    }

    @PutMapping("/rename_branch/{userId}/{repositoryName}")
    public ResponseEntity<RenameBranchResponse> renameBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch,
            @RequestParam String newBranch
    ) throws GitAPIException, IOException {
        String newBranchName = branchService.renameBranch(
                new RepositoryContext(userId, repositoryName),
                branch,
                newBranch
        );
        return ResponseEntity.ok(new RenameBranchResponse(newBranchName));
    }
}
