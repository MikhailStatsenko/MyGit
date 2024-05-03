package com.vcs.vitalitygit.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService {
    UserDetailsService userDetailsService();
    List<String> getAllUserNames();
}
