package com.vcs.mygit.git;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.CommitResponse;
import com.vcs.mygit.git.service.impl.CommandServiceImpl;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;


@RestController
@RequestMapping("/git")
@RequiredArgsConstructor
public class CommandController {
    private final CommandServiceImpl gitService;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);

    //TODO: location must lead to getFiles controller method
    @PostMapping("/init/{userId}/{repositoryName}")
    public ResponseEntity<String> initializeRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException, IOException {
        gitService.init(
                new RepositoryContext(userId, repositoryName)
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location)
                .body(location.toString());
    }

    @PostMapping("/commit/{userId}/{repositoryName}")
    public ResponseEntity<CommitResponse> commitChanges(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false, defaultValue = "New commit") String message
    ) throws GitAPIException, IOException {
        RevCommit commitInfo = gitService.commit(
                new RepositoryContext(userId, repositoryName),
                message
        );
        Date commitDate = commitInfo.getAuthorIdent().getWhen();
        return ResponseEntity.ok(new CommitResponse(
                commitInfo.getId().getName(),
                dateFormatter.format(commitDate),
                commitInfo.getFullMessage()
        ));
    }

    @PostMapping("/add/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> addFileToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false) String pattern
    ) throws GitAPIException, IOException {
        Set<String> addedFiles = gitService.add(
                new RepositoryContext(userId, repositoryName),
                pattern
        );
        return ResponseEntity.ok(addedFiles);
    }

    @PostMapping("/addAll/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> addAllFilesToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException, IOException {
        Set<String> addedFiles = gitService.addAll(new RepositoryContext(userId, repositoryName));
        return ResponseEntity.ok(addedFiles);
    }
}

