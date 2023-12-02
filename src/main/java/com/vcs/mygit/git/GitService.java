package com.vcs.mygit.git;

import com.vcs.mygit.exception.NothingToCommitException;
import com.vcs.mygit.exception.RepositoryNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service class handling Git operations.
 * Methods in this class with parameters {@code userId} and {@code repositoryPath}
 * are intercepted by the aspect {@link com.vcs.mygit.aspect.RepositoryPathValidationAspect}.
 * This aspect validates {@param userId} and {@param repositoryName} by checking if they are
 * null or blank
 *
 * @see com.vcs.mygit.aspect.RepositoryPathValidationAspect
 */
@Service
public class GitService {
    private Git openGitRepository(Path repositoryPath) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(repositoryPath.resolve(".git").toFile())
                .readEnvironment()
                .findGitDir()
                .build();
        return new Git(repository);
    }

    private void checkIfRepositoryExits(Path path) {
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            throw new RepositoryNotFoundException("Repository does not exist");
        }
    }
    public void init(String userId, String repositoryName) throws GitAPIException {
        String repositoryPath = userId + "/" + repositoryName;

        if (Files.exists(Path.of(repositoryPath)))
            throw new IllegalArgumentException("Repository already exists");
        try (Git ignored = Git.init()
                .setDirectory(new File(repositoryPath))
                .call()
        ) {}
    }

    public void commit(String userId, String repositoryName, String message) throws IOException, GitAPIException {
        String repositoryPath = userId + "/" + repositoryName;

        var path = Path.of(repositoryPath);

        checkIfRepositoryExits(path);
        try (Git git = openGitRepository(path)) {
            Status status = git.status().call();
            if (status.getAdded().isEmpty() && status.getChanged().isEmpty()) {
                throw new NothingToCommitException("Nothing to commit, working directory clean");
            }
            git.commit().setMessage(message).call();
        }
    }

    public void add(String userId, String repositoryName, String filePath) throws IOException, GitAPIException {
        if (filePath == null || filePath.isBlank())
            throw new IllegalArgumentException("File path parameter must be present");

        String repositoryPath = userId + "/" + repositoryName;

        if (!Files.exists(Path.of(repositoryPath + "/" + filePath)))
            throw new IllegalArgumentException("No such file or directory");

        try (Git git = openGitRepository(Path.of(repositoryPath))) {
            git.add().addFilepattern(filePath).call();
        }
    }

    public void addAll(String userId, String repositoryName) throws IOException, GitAPIException {
        add(userId, repositoryName, ".");
    }

    public void createRepositoryArchive(String userId, String repositoryName, HttpServletResponse response) throws IOException {
        String repositoryPath = userId + "/" + repositoryName;

        Path repositoryDirectory = Paths.get(repositoryPath);

        checkIfRepositoryExits(repositoryDirectory);

        String archiveFileName = repositoryName + ".zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=" + archiveFileName);

        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            Files.walkFileTree(repositoryDirectory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filePath = repositoryDirectory.relativize(file).toString();
                    zipOut.putNextEntry(new ZipEntry(filePath));
                    Files.copy(file, zipOut);
                    zipOut.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public Map<String, String> uploadFiles(
            String userId,
            String repositoryName,
            MultipartFile[] files
    ) throws IOException, GitAPIException {
        if (files == null || files.length == 0)
            throw new IllegalArgumentException("There are no files to upload");

        String repositoryPath = userId + "/" + repositoryName;
        Path repositoryDirectory = Paths.get(repositoryPath);

        if (!Files.exists(repositoryDirectory)) {
            init(userId, repositoryName);
        }

        Map<String, String> addedFiles = new HashMap<>();
        for (MultipartFile file : files) {
            String fileName = file.getOriginalFilename();

            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("The file must have non-blank name");
            }
            Path filePath = repositoryDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            addedFiles.put(fileName, filePath.toString());
        }
        return addedFiles;
    }

    public void createBranch(String userId, String repositoryName, String branchName) throws IOException, GitAPIException {
        String repositoryPath = userId + "/" + repositoryName;

        try (Git git = openGitRepository(Path.of(repositoryPath))) {
            git.branchCreate().setName(branchName).call();
        }
    }

    public void switchBranch(String userId, String repositoryName, String branchName) throws IOException, GitAPIException {
        String repositoryPath = userId + "/" + repositoryName;

        try (Git git = openGitRepository(Path.of(repositoryPath))) {
            git.checkout().setName(branchName).call();
        }
    }

    public List<String> listBranches(String userId, String repositoryName) throws IOException, GitAPIException {
        String repositoryPath = userId + "/" + repositoryName;

        try (Git git = openGitRepository(Path.of(repositoryPath))) {
            return git.branchList().call()
                    .stream()
                    .map(ref -> ref.getName().substring(ref.getName().lastIndexOf('/') + 1))
                    .collect(Collectors.toList());
        }
    }
}
