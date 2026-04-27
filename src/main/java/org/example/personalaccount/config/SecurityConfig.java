package org.example.personalaccount.config;


import lombok.RequiredArgsConstructor;
import org.example.personalaccount.service.CustomUserDetailsService;
import org.example.personalaccount.service.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

//Отключаем Спринг Сисуритцу
@Configuration // Указывает Spring, что внутри находятся определения бинов. Их нужно создать и настроить при запуске
@EnableWebSecurity // Включает модуль безопасности
@RequiredArgsConstructor // Генерирует конструктор для final полей
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;


    @Bean
    // Настраивает цепочку фильтров
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // Активирует поддержку CORS, нужно, чтобы фронт общался с бэком
                .csrf(csrf -> csrf.disable()) // Отключаем для REST API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Разрешаем логин и регистрацию без токена
                        .anyRequest().authenticated() // Все остальное — только по токену
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Никаких сессий на сервере
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Наш фильтр идет ПЕРВЫМ

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Список разрешенных адресов
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        // Список разрешенных действий
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Разрешает передавать заголовок
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    // Находит пользователя и проверяет пароль
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService); // Пустые скобки!

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    // Создает инструмент для шифрования
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

