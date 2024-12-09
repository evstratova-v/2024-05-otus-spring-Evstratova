package ru.otus.project.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.auth.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
