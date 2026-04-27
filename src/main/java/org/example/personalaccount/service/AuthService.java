package org.example.personalaccount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.personalaccount.dto.LoginRequest;
import org.example.personalaccount.dto.RegisterRequest;
import org.example.personalaccount.dto.UserResponse;
import org.example.personalaccount.exception.trueExistsByEmail;
import org.example.personalaccount.model.Role;
import org.example.personalaccount.model.User;
import org.example.personalaccount.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j // Автоматически создает объект логгера log
@Service // Помечаем класс, как сервис
@RequiredArgsConstructor // Автоматически создает конструктор для полей final
// Класс создает аккаунты, проверяет пароли, выдает токены
public class AuthService {
    private final UserRepository userRepository; // Доступ к таблице БД
    private final BCryptPasswordEncoder passwordEncoder; // Инструмент для работы с паролями. Умеет превращать пароль в хеш и проверять их
    private final JwtService jwtService; // Сервис для генерации JWT - токенов

    // Метод превращает данные из формы регистрации в запись в БД
    public String registerUser(RegisterRequest registerRequest) {
        // Проверяем, есть ли уже такой пользователь
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new trueExistsByEmail("ТАКОЙ ОБЪЕКТ УЖЕ ЕСТЬ!!!");

        }
        // Хешируем пароль
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // Создаем новый объект
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);

        // Отправляем данные в БД
        userRepository.save(user);
        log.info("Пользователь {} успешно сохранен!", user.getId());

        return user.getEmail();

    }

    // Проверка личности
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

        // Выдаем токен для всех остальных запросов
        return jwtService.generateToken(user.getEmail());
    }

    // Метод для того, чтобы показать пользователю его данные
    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        log.info("--- ПЫТАЕМСЯ НАЙТИ В БД: [{}] ---",  email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Маппинг (перекладывание данных) из User в UserResponse
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}
