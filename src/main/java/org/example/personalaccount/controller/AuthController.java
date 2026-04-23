package org.example.personalaccount.controller;

import lombok.RequiredArgsConstructor;
import org.example.personalaccount.dto.LoginRequest;
import org.example.personalaccount.dto.RegisterRequest;
import org.example.personalaccount.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // Метод для регистрации нового пользователя
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("Пользователь успешно зарегистрирован!");
    }

    // Метод для входа и получения JWT-токена
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);

        // Возвращаем токен в виде JSON объекта {"token": "eyJhbG..."}
        return ResponseEntity.ok(Map.of("token", token));
    }
}
