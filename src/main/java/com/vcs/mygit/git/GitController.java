package com.vcs.mygit.git;

import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/git")
@RequiredArgsConstructor
public class GitController {
    private final GitService gitService;

    @PostMapping("/upload/{userId}/{repositoryName}")
    public ResponseEntity<Map<String, String>> uploadFilesToWorkspace(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws GitAPIException, IOException {
        Map<String, String> uploadedFiles = gitService.uploadFiles(userId, repositoryName, files);
        return ResponseEntity.ok(uploadedFiles);
    }

    //TODO: location must lead to getFiles controller method
    @PostMapping("/init/{userId}/{repositoryName}")
    public ResponseEntity<String> initializeRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException {
        gitService.init(userId, repositoryName);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUri();
        return ResponseEntity.created(location)
                .body(location.toString());
    }

    @PostMapping("/commit/{userId}/{repositoryName}")
    public ResponseEntity<String> commitChanges(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false, defaultValue = "New commit") String message
    ) throws GitAPIException, IOException {
        gitService.commit(userId, repositoryName, message);
        return ResponseEntity.ok("Changes committed to repository " + repositoryName);
    }

    @PostMapping("/add/{userId}/{repositoryName}")
    public ResponseEntity<String> addFileToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(required = false) String path
    ) throws GitAPIException, IOException {
        gitService.add(userId, repositoryName, path);
        return ResponseEntity.ok(repositoryName + ": file " + path + " successfully added to staging area");
    }

    @PostMapping("/addAll/{userId}/{repositoryName}")
    public ResponseEntity<String> addAllFilesToRepository(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException, IOException {
        gitService.addAll(userId, repositoryName);
        return ResponseEntity.ok(repositoryName + ": successfully added all files to staging area");
    }

    @GetMapping("/clone/{userId}/{repositoryName}")
    public void downloadRepositoryArchive(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletResponse response
    ) throws IOException {
        gitService.createRepositoryArchive(userId, repositoryName, response);
    }

    @PostMapping("/createBranch/{userId}/{repositoryName}")
    public ResponseEntity<String> createBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branch
    ) throws GitAPIException, IOException {
        gitService.createBranch(userId, repositoryName, branch);
        return ResponseEntity.ok("Branch " + branch + " created in repository " + repositoryName);
    }

    @PostMapping("/switchBranch/{userId}/{repositoryName}")
    public ResponseEntity<String> switchBranch(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String branchName
    ) throws GitAPIException, IOException {
        gitService.switchBranch(userId, repositoryName, branchName);
        return ResponseEntity.ok("Switched to branch " + branchName + " in repository " + repositoryName);
    }

    @GetMapping("/listBranches/{userId}/{repositoryName}")
    public ResponseEntity<List<String>> listBranches(
            @PathVariable String userId,
            @PathVariable String repositoryName
    ) throws GitAPIException, IOException {
        List<String> branches = gitService.listBranches(userId, repositoryName);
        return ResponseEntity.ok(branches);
    }
}

