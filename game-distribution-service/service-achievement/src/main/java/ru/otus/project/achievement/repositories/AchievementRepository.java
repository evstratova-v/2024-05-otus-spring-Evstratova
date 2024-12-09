package ru.otus.project.achievement.repositories;

import ru.otus.project.achievement.models.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findAllByGameIdAndIsHiddenFalse(long gameId);

    int countByGameIdAndIsHiddenTrue(long gameId);

    void deleteAllByGameId(long gameId);
}
