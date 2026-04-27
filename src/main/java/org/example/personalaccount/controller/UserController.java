package org.example.personalaccount.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController // Говорит Spring, что результат метода нужно превращать в JSON и отправлять ответ клиенту
@RequestMapping("/api/users") // Задает базовый путь
public class UserController {
    @GetMapping("/me") // Слушает HTTP запросы по полному адресу
    // Метод возвращает мапу с email пользователя
    public Map<String, String> getCurrentUser(Authentication authentication) {
        return Map.of("email", authentication.getName());
    }
}


