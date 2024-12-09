package ru.otus.project.achievement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.project.achievement.models.Achievement;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDto {
    @Min(1)
    private long id;

    @Min(1)
    private long gameId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean isHidden;

    public static AchievementDto toDto(Achievement achievement) {
        return new AchievementDto(achievement.getId(),
                achievement.getGameId(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.isHidden());
    }

    public static Achievement toDomainObject(AchievementDto achievementDto) {
        return new Achievement(achievementDto.getId(),
                achievementDto.getGameId(),
                achievementDto.getName(),
                achievementDto.getDescription(),
                achievementDto.isHidden());
    }
}
