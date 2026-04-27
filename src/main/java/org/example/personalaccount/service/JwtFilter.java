package org.example.personalaccount.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Говорит, что нужно сканировать этот класс и создать из него в бин
@RequiredArgsConstructor // Автоматически создает конструктор для полей final
public class JwtFilter extends OncePerRequestFilter // Фильтр срабатывает ровно один раз за один HTTP - запрос
{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Встроенный сервис Spring Security

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request, // Входящий запрос (берем заголовки)
            @NonNull HttpServletResponse response, // Исходящий ответ (сюда можно записать что то, если доступа нет
            @NonNull FilterChain filterChain // Для передачи запроса в контроллер
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); // Извлечение заголовка
        final String jwt;
        final String userEmail;

        // Проверяем, есть ли заголовок Authorization и начинается ли он с "Bearer "
        // Если нет, то передаем запрос дальше и выходим из фильтра
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем сам токен (отрезаем "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractEmail(jwt); // Достаем email

        // Если email есть, а пользователь еще не аутентифицирован в текущем контексте
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Загрузка пользователя из БД
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Если токен валиден, то создаем объект аутентификации и кладем его в контекст
            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, // Нужно передавать весь объект!
                        null,
                        userDetails.getAuthorities()
                );
                // Добавляем в объект аутентификации доп. данные
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Теперь Spring Security знает, что пользователь вошел!
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Передаем запрос дальше по цепочке
        filterChain.doFilter(request, response);
    }


}
