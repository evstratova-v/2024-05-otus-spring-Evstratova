package ru.otus.project.achievement.services;

import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.AchievementEventDto;

import java.util.List;

public interface AchievementHistoryService {

    List<AchievementEventDto> findAchievementHistoryByUserId(long userId);

    List<AchievementEventDto> findAchievementHistoryByUserIdAndGameId(long userId, long gameId);

    AchievementEventDto insert(long userId, AchievementDto achievementDto);
}
