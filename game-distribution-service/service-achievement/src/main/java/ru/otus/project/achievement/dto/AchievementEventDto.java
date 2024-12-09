package ru.otus.project.achievement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.achievement.models.AchievementEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Data
@AllArgsConstructor
public class AchievementEventDto {

    private long gameId;

    private String achievementName;

    private LocalDateTime eventTime;

    private long userId;

    public static AchievementEventDto toDto(AchievementEvent achievementEvent) {
        return new AchievementEventDto(achievementEvent.getAchievement().getGameId(),
                achievementEvent.getAchievement().getName(),
                achievementEvent.getEventTime().atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime(),
                achievementEvent.getUserId()
        );
    }
}
