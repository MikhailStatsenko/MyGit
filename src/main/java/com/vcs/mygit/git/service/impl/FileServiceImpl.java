package com.vcs.mygit.git.service.impl;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.UploadFilesResponse;
import com.vcs.mygit.git.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Override
    public Object getFileOrDirectoryContents(RepositoryContext repoContext, String path) throws IOException {
        Path targetPath = repoContext.getRepositoryPath();
        if (path != null && !path.isBlank()) {
            targetPath = targetPath.resolve(path);
        }

        if (Files.isDirectory(targetPath)) {
            return getDirectoryContents(targetPath);
        } else if (Files.isRegularFile(targetPath)) {
            return getFileContent(targetPath);
        } else {
            throw new IllegalArgumentException("The path does not exist or is neither a file nor a directory");
        }
    }

    @Override
    public String getFileContent(Path filePath) throws IOException {
        if (!Files.isRegularFile(filePath)) {
            throw new IllegalArgumentException("The path does not point to a file");
        }
        return Files.readString(filePath);
    }

    @Override
    public Map<String, String> getDirectoryContents(Path dirPath) throws IOException {
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("Invalid directory path");
        }

        Map<String, String> directoryContents = new LinkedHashMap<>();
        try (Stream<Path> paths = Files.list(dirPath)) {
            paths.filter(p -> {
                        try {
                            return !Files.isHidden(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(p -> {
                String fileName = p.getFileName().toString();
                String type = Files.isDirectory(p) ? "directory" : "file";
                directoryContents.put(fileName, type);
            });
        }
        return directoryContents;
    }

    private Set<String> getExistingFiles(Path directoryPath) throws IOException {
        Set<String> existingFiles = new HashSet<>();
        try (var stream =  Files.walk(directoryPath)) {
            stream.filter(Files::isRegularFile)
                    .forEach(file -> {
                        Path relativePath = directoryPath.relativize(file);
                        existingFiles.add(relativePath.toString());
                    });
        }
        return existingFiles;
    }

    @Override
    public UploadFilesResponse uploadFiles(
            RepositoryContext repoContext,
            MultipartFile[] files
    ) throws IOException {
        if (files == null || files.length == 0)
            throw new IllegalArgumentException("There are no files to upload");

        Path repositoryPath = repoContext.getRepositoryPath();

        Set<String> existingFiles = getExistingFiles(repositoryPath);

        List<String> rejectedFiles = new ArrayList<>();
        Map<String, String> addedFiles = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("The file must have non-blank name");
            }

            if (existingFiles.contains(fileName)) {
                rejectedFiles.add(fileName);
                continue;
            }

            Path filePath = repositoryPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            addedFiles.put(fileName, filePath.toString());
        }
        return new UploadFilesResponse(rejectedFiles, addedFiles);
    }

    @Override
    public void getRepositoryArchive(
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

    @Override
    public List<String> deleteFile(RepositoryContext repoContext, String path) throws IOException {
        Path pathInRepository = repoContext.getRepositoryPath().resolve(path);

        if (!isPathAllowedToDelete(pathInRepository)) {
            throw new IllegalArgumentException("Operation not permitted");
        }

        if (!Files.exists(pathInRepository)) {
            throw new IllegalArgumentException("No such file or directory");
        }

        if (Files.isDirectory(pathInRepository)) {
            return deleteDirectory(pathInRepository);
        } else {
            Files.delete(pathInRepository);
            return Collections.singletonList(pathInRepository.toString());
        }
    }

    private boolean isPathAllowedToDelete(Path path) {
        List<String> pathSegments = Arrays.stream(path.toString().split("/")).toList();
        return pathSegments.size() > 2;
    }

    private List<String> deleteDirectory(Path directoryPath) throws IOException {
        List<String> deletedFiles = new ArrayList<>();
        Files.walkFileTree(directoryPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                deletedFiles.add(file.toString());
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        return deletedFiles;
    }
}
