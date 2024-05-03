package com.vcs.vitalitygit.service;

import com.vcs.vitalitygit.domain.model.User;
import com.vcs.vitalitygit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetailsService userDetailsService() {
        return username -> userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<String> getAllUserNames() {
        return userRepository.findAll().stream().map(User::getUsername).toList();
    }
}
