package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.user.UserDto;
import com.vcs.vitalitygit.domain.dto.user.UsersNamesResponse;
import com.vcs.vitalitygit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<UsersNamesResponse> allUserNames() {
        List<String> userNames = userService.getAllUserNames();
        return ResponseEntity.ok(new UsersNamesResponse(userNames));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        var user = userService.getUserOptionalByUsername(username).map(UserDto::new)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/pattern")
    public ResponseEntity<UsersNamesResponse> getUsersNamesByPattern(@RequestParam String pattern) {
        List<String> userNames = userService.getAllUserNamesByPattern(pattern);
        return ResponseEntity.ok(new UsersNamesResponse(userNames));
    }
}
