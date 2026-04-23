package org.example.personalaccount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.example.personalaccount.dto.LoginRequest;
import org.example.personalaccount.dto.RegisterRequest;
import org.example.personalaccount.dto.UserResponse;
import org.example.personalaccount.exception.trueExistsByEmail;
import org.example.personalaccount.model.Role;
import org.example.personalaccount.model.User;
import org.example.personalaccount.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
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
        log.info("Пользователь {} успешно сохранен!", user.getId());

        return user.getEmail();

    }

    public String login(LoginRequest request) {
        // 1. Ищем пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 2. Сравниваем хешированный пароль из БД с тем, что ввел пользователь
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.error("Неверный пароль для пользователя {}", user.getId());
            throw new RuntimeException("Неверный логин или пароль");
        }

        log.info("Пользователь {} успешно вошел", user.getId());

        return jwtService.generateToken(user.getEmail());
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println("--- ПЫТАЕМСЯ НАЙТИ В БД: [" + email + "] ---"); // Посмотрите в консоль при запросе

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Маппинг (перекладывание данных) из User в UserResponse
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }


}
