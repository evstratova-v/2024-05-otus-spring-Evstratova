package ru.otus.project.achievement.repositories;

import ru.otus.project.achievement.models.Achievement;
import ru.otus.project.achievement.models.AchievementEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementEventRepository extends JpaRepository<AchievementEvent, Long> {
    List<AchievementEvent> findAllByUserId(long userId);

    List<AchievementEvent> findAllByUserIdAndAchievementGameId(long userId, long gameId);

    boolean existsByUserIdAndAchievement(long userId, Achievement achievement);
}
