package com.vcs.mygit.git;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.UploadFilesResponse;
import com.vcs.mygit.git.service.FileService;
import com.vcs.mygit.git.util.PathExtractor;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @GetMapping("/{userId}/{repositoryName}/**")
    public ResponseEntity<?> getFileOrDirectoryContents(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest request
    ) throws IOException {
        String path = PathExtractor.extractPathFromRequest(request);
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        return ResponseEntity.ok(fileService.getFileOrDirectoryContents(repositoryContext, path));
    }

    @PostMapping("/upload/{userId}/{repositoryName}")
    public ResponseEntity<UploadFilesResponse> uploadFilesToWorkingDirectory(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) throws GitAPIException, IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        UploadFilesResponse response = fileService.uploadFiles(repositoryContext, files);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{userId}/{repositoryName}")
    public void downloadRepositoryArchive(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletResponse response
    ) throws IOException {
        var repositoryContext = new RepositoryContext(userId, repositoryName);
        fileService.getRepositoryArchive(repositoryContext, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
