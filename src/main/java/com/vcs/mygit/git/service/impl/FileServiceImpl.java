package com.vcs.mygit.git.service.impl;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.service.CommandService;
import com.vcs.mygit.git.service.FileService;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final CommandService commandService;

    // TODO: check exception when files have the same name
    public Map<String, String> uploadFiles(
            RepositoryContext repoContext,
            MultipartFile[] files
    ) throws IOException, GitAPIException {
        if (files == null || files.length == 0)
            throw new IllegalArgumentException("There are no files to upload");

        Path repositoryPath = repoContext.getRepositoryPath();

        if (!Files.exists(repositoryPath)) {
            commandService.init(repoContext);
        }

        Map<String, String> addedFiles = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("The file must have non-blank name");
            }
            Path filePath = repositoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            addedFiles.put(fileName, filePath.toString());
        }
        return addedFiles;
    }

    public void createRepositoryArchive(
            RepositoryContext repoContext,
            HttpServletResponse response
    ) throws IOException {
        Path repositoryPath = repoContext.getRepositoryPath();

        String repositoryName = repoContext.repositoryName();
        String archiveFileName = repositoryName + ".zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + archiveFileName);

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            Files.walkFileTree(repositoryPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filePath = repositoryPath.relativize(file).toString();
                    zipOut.putNextEntry(new ZipEntry(filePath));
                    Files.copy(file, zipOut);
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
