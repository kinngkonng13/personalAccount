package org.example.personalaccount.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

// Класс для создания и проверки токенов
@Service // Помечаем класс, как сервис
public class JwtService {
    // Секретная строка, известная только серверу
    // С ее помощью создается уникальная цифровая подпись
    private static final String SECRET_STRING = "your-256-bit-secret-your-256-bit-secret-your-256-bit-secret";
    // Превращаем текстовую строку в криптографический ключ с помощью алгоритма
    private final Key key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    // Время жизни токена
    private final long EXPIRATION_TIME = 86400000; // 24 часа в миллисекундах

    // Создание токена
    // Метод превращает данные пользователя в зашифрованную строку
    public String generateToken(String email) {
        return Jwts.builder() // Запускаем конструктор токена
                .setSubject(email) // Записывает в центр токена email
                .setIssuedAt(new Date()) // Ставит дату создания
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Время смерти токена
                .signWith(key, SignatureAlgorithm.HS256) // Все данные выше "запечатываются" секретным ключом
                .compact(); // Собирает все в одну длинную строку
    }

    // Чтение данных
    // Метод расшифровывает токен с помощью того же ключа и достает email
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Чтение данных
    // Метод расшифровывает токен с помощью того же ключа и достает email
    private Date extractExpiration(String token) {
        return Jwts.parser() // Запускает дешифратор
                .setSigningKey(key) // Говорит использовать этот ключ
                .parseClaimsJws(token) // Разрезает токен и проверяет не подделан ли он
                .getBody()
                .getExpiration(); // Достает из центра email
    }

    // Сравнивает дату токена и сравнивает его с текущей датой на сервере
    private boolean isTokenExpired(String token) {
        //true если токен просрочен
        // false если токен действующий
        return extractExpiration(token).before(new Date());
    }

    // Финальная проверка
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token); // Достаем email из токена

        // Проверка: действительно ли этот email того пользователя из БД
        // Не просрочен ли он
        // Если оба true, то пользователь входит
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}

