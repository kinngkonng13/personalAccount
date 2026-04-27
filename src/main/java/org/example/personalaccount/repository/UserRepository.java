package org.example.personalaccount.repository;

import org.example.personalaccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Помечаем интерфейс как репозиторий
@Repository
// Интерфейс для прямого обращения к таблице БД
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}

