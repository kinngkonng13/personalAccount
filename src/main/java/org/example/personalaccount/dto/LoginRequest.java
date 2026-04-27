package org.example.personalaccount.dto;

import lombok.Data;

// Класс для процесса аутентификации
@Data
public class LoginRequest {
    private String email;
    private String password;
}
