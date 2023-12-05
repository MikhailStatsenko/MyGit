package com.vcs.mygit.git;

import com.vcs.mygit.annotation.RepositoryOwnerAccess;
import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.CommitResponse;
import com.vcs.mygit.git.service.impl.CommandServiceImpl;
import com.vcs.mygit.git.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Set;


@RestController
@RequestMapping("api/git")
@RequiredArgsConstructor
public class CommandController {
    private final CommandServiceImpl gitService;

    @RepositoryOwnerAccess
    @PostMapping("/init/{userId}/{repositoryName}")
    public ResponseEntity<String> initializeRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest request
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        gitService.init(repositoryContext);
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
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        RevCommit commitInfo = gitService.commit(repositoryContext, message);
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
            @RequestParam(required = false) String pattern,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        Set<String> addedFiles = gitService.add(repositoryContext, pattern);
        return ResponseEntity.ok(addedFiles);
    }

    @RepositoryOwnerAccess
    @PostMapping("/addAll/{userId}/{repositoryName}")
    public ResponseEntity<Set<String>> addAllFilesToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest httpServletRequest
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        Set<String> addedFiles = gitService.addAll(repositoryContext);
        return ResponseEntity.ok(addedFiles);
    }
}

