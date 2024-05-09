package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.user.UserDto;
import com.vcs.vitalitygit.domain.dto.user.UsersNamesResponse;
import com.vcs.vitalitygit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<UsersNamesResponse> allUserNames() {
        List<UserDto> userNames = userService.getAllUsers().stream().map(UserDto::new).toList();
        return ResponseEntity.ok(new UsersNamesResponse(userNames));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable String username) {
        var user = userService.getUserByUsername(username);
        return ResponseEntity.ok(new UserDto(user));
    }

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser() {
        var user = userService.getCurrentUser();
        return ResponseEntity.ok(new UserDto(user));
    }

    @GetMapping("/pattern")
    public ResponseEntity<UsersNamesResponse> getUsersNamesByPattern(@RequestParam String pattern) {
        List<UserDto> userNames = userService.getAllUsersByPattern(pattern).stream().map(UserDto::new).toList();
        return ResponseEntity.ok(new UsersNamesResponse(userNames));
    }
}
