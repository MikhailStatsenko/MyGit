package com.vcs.vitalitygit.servlet;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.util.GitRepositoryUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.http.server.resolver.DefaultReceivePackFactory;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.PostReceiveHook;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.eclipse.jgit.transport.resolver.ServiceNotEnabledException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class VitalityGitServlet implements GitRepositoryUser {
    @Bean
    public ServletRegistrationBean<GitServlet> gitServlet() {
        GitServlet gitServlet = new GitServlet();

        gitServlet.setRepositoryResolver((req, name) -> {
            try {
                Path rootPath = Path.of(RepositoryDetails.rootPath());
                Path repoPath = Path.of(name);

                var repository = getClosedRepository(rootPath.resolve(repoPath));
                repository.incrementOpen();

                return repository;
            } catch (IOException e) {
                log.error("Error while processing HTTP request: {}", req.getRequestURL(), e);
                throw new ServiceNotEnabledException();
            }
        });
        gitServlet.setReceivePackFactory(new ReceivePackFactory());

        ServletRegistrationBean<GitServlet> registrationBean = new ServletRegistrationBean<>(gitServlet, "/repository/*");
        registrationBean.setLoadOnStartup(1);

        return registrationBean;
    }

    public static class ReceivePackFactory extends DefaultReceivePackFactory {
        private static final PostReceiveHook updateRefHook = (rp, commands) -> {
            try (Git git = new Git(rp.getRepository())){
                git.reset().setMode(ResetCommand.ResetType.HARD).call();
            } catch (Exception e) {
                log.error("Error updating files ", e);
            }
        };

        @Override
        public ReceivePack create(HttpServletRequest req, Repository db)
                throws ServiceNotEnabledException, ServiceNotAuthorizedException {

            ReceivePack receivePack = super.create(req, db);
            receivePack.setPostReceiveHook(updateRefHook);
            return receivePack;
        }
    }
}

