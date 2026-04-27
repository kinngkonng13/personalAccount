package org.example.personalaccount.service;

import lombok.RequiredArgsConstructor;
import org.example.personalaccount.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Помечаем класс, как сервис
@RequiredArgsConstructor // Автоматически создает конструктор для полей final
public class CustomUserDetailsService implements UserDetailsService {
    // Интерфейс содержит методы прямого обращения к таблице БД
    private final UserRepository userRepository;

    // Вызов метода происходит каждый раз, когда Spring нужно знать подробности о пользователе
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            return userRepository.findByEmail(email) // Сам запрос к БД
                    // Если пользователь найден, он возвращается
                    // Иначе, выбрасывается исключение
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь с email " + email + " не найден"));

    }
}
