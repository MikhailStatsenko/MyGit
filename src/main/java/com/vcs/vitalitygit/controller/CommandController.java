package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.annotation.RepositoryOwnerAccess;
import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.comand.CommitResponse;
import com.vcs.vitalitygit.domain.dto.comand.LogInfoElementResponse;
import com.vcs.vitalitygit.domain.dto.comand.StatusResponse;
import com.vcs.vitalitygit.service.CommandService;
import com.vcs.vitalitygit.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("api/git")
@RequiredArgsConstructor
public class CommandController {
    private final CommandService commandService;

    @RepositoryOwnerAccess
    @PostMapping("/init/{userId}/{repositoryName}")
    public ResponseEntity<String> initializeRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        commandService.init(repositoryContext);
        String basePath = request.getRequestURL().toString().replace("/git/init", "/files");
        URI location = URI.create(basePath);
        return ResponseEntity.created(location).build();
    }

    @RepositoryOwnerAccess
    @PostMapping("/commit/{userId}/{repositoryName}")
    public ResponseEntity<CommitResponse> commitChanges(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false, defaultValue = "New commit") String message,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        RevCommit commitInfo = commandService.commit(repositoryContext, message);
        Date commitDate = commitInfo.getAuthorIdent().getWhen();
        return ResponseEntity.ok(new CommitResponse(
                commitInfo.getId().getName(),
                DateFormatter.format(commitDate),
                commitInfo.getFullMessage()
        ));
    }

    @RepositoryOwnerAccess
    @PostMapping("/add/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> addFileToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String pattern,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        Set<String> addedFiles = commandService.add(repositoryContext, pattern);
        return ResponseEntity.ok(addedFiles);
    }

    @RepositoryOwnerAccess
    @PostMapping("/addAll/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> addAllFilesToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        Set<String> addedFiles = commandService.addAll(repositoryContext);
        return ResponseEntity.ok(addedFiles);
    }

    @RepositoryOwnerAccess
    @PostMapping("/remove/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> removeFileFromStagingArea(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String pattern,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        Set<String> removedFiles = commandService.remove(repositoryContext, pattern);
        return ResponseEntity.ok(removedFiles);
    }

    @RepositoryOwnerAccess
    @GetMapping("/log/{userId}/{repositoryName}")
    public ResponseEntity<List<LogInfoElementResponse>> getLog(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false, defaultValue = "master") String branch,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        List<LogInfoElementResponse> commitLog = commandService.log(repositoryContext, branch);
        return ResponseEntity.ok(commitLog);
    }

    @RepositoryOwnerAccess
    @GetMapping("/status/{userId}/{repositoryName}")
    public ResponseEntity<StatusResponse> getRepositoryStatus(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        StatusResponse statusResponse = commandService.status(repositoryContext);
        return ResponseEntity.ok(statusResponse);
    }
}

