package org.example.personalaccount.controller;

import lombok.RequiredArgsConstructor;
import org.example.personalaccount.dto.UserResponse;
import org.example.personalaccount.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getMyProfile() {
//        return ResponseEntity.ok(authService.getCurrentUser());
//    }

    @GetMapping("/me")
    public Map<String, String> getCurrentUser(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}


