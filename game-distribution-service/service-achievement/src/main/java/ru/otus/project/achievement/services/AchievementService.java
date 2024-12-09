package ru.otus.project.achievement.services;

import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.CreateAchievementRequest;

import java.util.List;
import java.util.Optional;

public interface AchievementService {

    Optional<AchievementDto> findById(long achievementId);

    List<AchievementDto> findAllByGameIdAndNotHidden(long gameId);

    int countByGameIdIsHiddenTrue(long gameId);

    AchievementDto insert(CreateAchievementRequest createAchievementRequest);

    AchievementDto update(AchievementDto achievementDto);

    void deleteById(long achievementId);

    void deleteByGameId(long gameId);
}
