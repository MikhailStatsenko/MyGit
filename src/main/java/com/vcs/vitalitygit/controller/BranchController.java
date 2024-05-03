package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.branch.request.BranchRequest;
import com.vcs.vitalitygit.domain.dto.branch.request.MergeBranchRequests;
import com.vcs.vitalitygit.domain.dto.branch.request.RenameBranchRequest;
import com.vcs.vitalitygit.domain.dto.branch.response.*;
import com.vcs.vitalitygit.domain.dto.comand.CommitResponse;
import com.vcs.vitalitygit.service.BranchService;
import com.vcs.vitalitygit.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/git/branch")
@RequiredArgsConstructor
public class BranchController {
    private final BranchService branchService;

    @PostMapping("/create")
    public ResponseEntity<CreateBranchResponse> createBranch(@Valid @RequestBody BranchRequest request
    ) throws GitAPIException, IOException {
        var repoDetails = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        String newBranch = branchService.createBranch(repoDetails, request.getBranch());
        String currentBranch = branchService.getCurrentBranch(repoDetails);

        return ResponseEntity.ok(new CreateBranchResponse(newBranch, currentBranch));
    }

    @PostMapping("/switch")
    public ResponseEntity<SwitchBranchResponse> switchBranch(@Valid @RequestBody BranchRequest request
    ) throws GitAPIException, IOException {
        var repoDetails = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        branchService.switchBranch(repoDetails, request.getBranch());
        String currentBranch = branchService.getCurrentBranch(repoDetails);

        return ResponseEntity.ok(new SwitchBranchResponse(currentBranch));
    }

    @GetMapping("/list")
    public ResponseEntity<ListBranchesResponse> listBranches(@Valid @RequestBody RepositoryDetails repoDetails
    ) throws GitAPIException, IOException {
        List<String> branches = branchService.listBranches(repoDetails);
        String currentBranch = branchService.getCurrentBranch(repoDetails);

        return ResponseEntity.ok(new ListBranchesResponse(currentBranch, branches));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteBranchResponse> deleteBranch(@Valid @RequestBody BranchRequest request
    ) throws GitAPIException, IOException {
        var repoDetails = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        String deletedBranch = branchService.deleteBranch(repoDetails, request.getBranch());

        return ResponseEntity.ok(new DeleteBranchResponse(deletedBranch));
    }

    @PutMapping("/rename")
    public ResponseEntity<RenameBranchResponse> renameBranch(@Valid @RequestBody RenameBranchRequest request
    ) throws GitAPIException, IOException {
        var repoDetails = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        String newBranchName = branchService.renameBranch(repoDetails, request.getBranch(), request.getNewBranchName());

        return ResponseEntity.ok(new RenameBranchResponse(newBranchName));
    }

    @PostMapping("/merge")
    public ResponseEntity<CommitResponse> mergeBranches(@Valid @RequestBody MergeBranchRequests request
    ) throws GitAPIException, IOException {
        var repoDetails = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        RevCommit mergeCommitInfo = branchService.mergeBranches(repoDetails, request.getBranch(), request.getBranchToMergeInto());
        Date commitDate = mergeCommitInfo.getAuthorIdent().getWhen();

        return ResponseEntity.ok(new CommitResponse(
                mergeCommitInfo.getId().getName(),
                DateFormatter.format(commitDate),
                mergeCommitInfo.getFullMessage()
        ));
    }
}
