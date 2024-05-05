package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.comand.request.CommitRequest;
import com.vcs.vitalitygit.domain.dto.comand.request.FileIndexRequest;
import com.vcs.vitalitygit.domain.dto.comand.request.LogRequest;
import com.vcs.vitalitygit.domain.dto.comand.response.CommitResponse;
import com.vcs.vitalitygit.domain.dto.comand.response.LogInfoElementResponse;
import com.vcs.vitalitygit.domain.dto.comand.response.StatusResponse;
import com.vcs.vitalitygit.service.CommandService;
import com.vcs.vitalitygit.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

    @PostMapping("/init")
    public ResponseEntity<String> initializeRepository(
            @Valid @RequestBody RepositoryDetails request,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        commandService.init(repositoryContext);
        String basePath = httpServletRequest.getRequestURL().toString().replace("/git/init", "/files");
        URI location = URI.create(basePath);

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/commit")
    public ResponseEntity<CommitResponse> commitChanges(@Valid @RequestBody CommitRequest commitRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(commitRequest.getUsername(), commitRequest.getRepositoryName());

        RevCommit commitInfo = commandService.commit(repositoryContext, commitRequest.getMessage());
        Date commitDate = commitInfo.getAuthorIdent().getWhen();

        return ResponseEntity.ok(new CommitResponse(
                commitInfo.getId().getName(),
                DateFormatter.format(commitDate),
                commitInfo.getFullMessage()
        ));
    }

    @PostMapping("/add")
    public ResponseEntity<Set<String>> addFileToIndex(@Valid @RequestBody FileIndexRequest request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        Set<String> addedFiles = commandService.add(repositoryContext, request.getPattern());

        return ResponseEntity.ok(addedFiles);
    }

    @PostMapping("/addAll")
    public ResponseEntity<Set<String>> addAllFilesToIndex(@Valid @RequestBody RepositoryDetails request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        Set<String> addedFiles = commandService.addAll(repositoryContext);

        return ResponseEntity.ok(addedFiles);
    }

    @PostMapping("/remove")
    public ResponseEntity<Set<String>> removeFileFromStagingArea(@Valid @RequestBody FileIndexRequest request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        Set<String> removedFiles = commandService.remove(repositoryContext, request.getPattern());

        return ResponseEntity.ok(removedFiles);
    }

    @GetMapping("/log")
    public ResponseEntity<List<LogInfoElementResponse>> getLog(@Valid @RequestBody LogRequest request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        List<LogInfoElementResponse> commitLog = commandService.log(repositoryContext, request.getBranch());

        return ResponseEntity.ok(commitLog);
    }

    @GetMapping("/status")
    public ResponseEntity<StatusResponse> getRepositoryStatus(@Valid @RequestBody RepositoryDetails request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryDetails(request.getUsername(), request.getRepositoryName());

        StatusResponse statusResponse = commandService.status(repositoryContext);

        return ResponseEntity.ok(statusResponse);
    }
}

