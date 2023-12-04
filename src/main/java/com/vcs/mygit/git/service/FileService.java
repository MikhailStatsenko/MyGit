package com.vcs.mygit.git.service;

import com.vcs.mygit.git.dto.RepositoryContext;
import com.vcs.mygit.git.dto.response.UploadFilesResponse;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface FileService {
    Object getFileOrDirectoryContents(RepositoryContext repoContext, String path) throws IOException;
    String getFileContent(Path filePath) throws IOException;
    Map<String, String> getDirectoryContents(Path dirPath) throws IOException;

    UploadFilesResponse uploadFiles(RepositoryContext repoContext, MultipartFile[] files)
            throws IOException, GitAPIException;

    void getRepositoryArchive(RepositoryContext repoContext, HttpServletResponse response) throws IOException;
}
