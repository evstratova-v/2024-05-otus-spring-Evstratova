package ru.otus.project.gameinfo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.gameinfo.models.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
