package ru.otus.project.gameinfo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.gameinfo.models.Developer;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
}
