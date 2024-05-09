package com.vcs.vitalitygit.service;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.model.User;
import com.vcs.vitalitygit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isCurrentUserOwnsRepository(RepositoryDetails repoContext) {
        return getCurrentUsername().equals(repoContext.getUsername());
    }

    public User getCurrentUser() {
        return userRepository.findUserByUsername(getCurrentUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getAllUsersByPattern(String pattern) {
        return userRepository.findAllByUsernameContainingIgnoreCase(pattern);
    }
}
