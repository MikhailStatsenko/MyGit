package com.vcs.mygit.user;

import com.vcs.mygit.user.dto.AllUsersNamesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/all")
    public ResponseEntity<AllUsersNamesResponse> allUserNames() {
        List<String> userNames = userService.getAllUserNames();
        return ResponseEntity.ok(new AllUsersNamesResponse(userNames));
    }
}
