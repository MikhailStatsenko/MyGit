package com.vcs.mygit.config;

//@Configuration
public class GitConfiguration {
//    @Bean
//    public ServletRegistrationBean<GitServlet> servletRegistrationBean() {
//        ServletRegistrationBean<GitServlet> registrationBean = new ServletRegistrationBean<>(new GitServlet(), "/git/*");
//        registrationBean.setLoadOnStartup(1);
//        registrationBean.addInitParameter("base-path", "path");
//        registrationBean.addInitParameter("repository-root", "/git");
//        return registrationBean;
//    }
//
//    @Bean
//    public Repository repository() throws IOException {
//        String repositoryPath = "path";
//        File gitDir = new File(repositoryPath);
//
//        if (!gitDir.exists()) {
//            if (!gitDir.mkdirs()) {
//                throw new IOException("Could not create repository directory: " + repositoryPath);
//            }
//
//            try (Repository repository = FileRepositoryBuilder.create(new File(repositoryPath, ".git"))) {
//                repository.create();
//            }
//        }
//
//        FileRepositoryBuilder builder = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment().findGitDir();
//
//        return builder.build();
//    }
}


