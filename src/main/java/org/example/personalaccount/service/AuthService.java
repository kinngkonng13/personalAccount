package org.example.personalaccount.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.example.personalaccount.dto.LoginRequest;
import org.example.personalaccount.dto.RegisterRequest;
import org.example.personalaccount.exception.trueExistsByEmail;
import org.example.personalaccount.model.Role;
import org.example.personalaccount.model.User;
import org.example.personalaccount.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Log log;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String registerUser(RegisterRequest registerRequest) {
        // Проверяем, есть ли уже такой пользователь
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new trueExistsByEmail("ТАКОЙ ОБЪЕКТ УЖЕ ЕСТЬ!!!");

        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword()); // Хешируем

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);

        userRepository.save(user);
        log.info("Пользователь успешно сохранен!");

        return user.getEmail();

    }

    public String login(LoginRequest request) {
        // 1. Ищем пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Сравниваем хешированный пароль из БД с тем, что ввел пользователь
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Неверный пароль для пользователя");
            throw new RuntimeException("Неверный логин или пароль");
        }

        log.info("Пользователь успешно вошел");

        return jwtService.generateToken(user.getEmail());
    }
}
