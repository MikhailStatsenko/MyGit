package com.vcs.mygit.git.service;

import com.vcs.mygit.git.dto.RepositoryContext;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface FileService {

    Map<String, String> uploadFiles(RepositoryContext repoContext, MultipartFile[] files)
            throws IOException, GitAPIException;

    void createRepositoryArchive(RepositoryContext repoContext, HttpServletResponse response)
            throws IOException;
}
