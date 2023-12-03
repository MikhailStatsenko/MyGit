package com.vcs.mygit.git;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.service.FileService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/git")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @PostMapping("/upload/{userId}/{repositoryName}")
    public ResponseEntity<Map<String, String>> uploadFilesToWorkspace(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws GitAPIException, IOException {
        Map<String, String> uploadedFiles = fileService.uploadFiles(
                new RepositoryContext(userId, repositoryName),
                files
        );
        return ResponseEntity.ok(uploadedFiles);
    }

    @GetMapping("/download/{userId}/{repositoryName}")
    public void downloadRepositoryArchive(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletResponse response
    ) throws IOException {
        fileService.createRepositoryArchive(
                new RepositoryContext(userId, repositoryName),
                response
        );
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
