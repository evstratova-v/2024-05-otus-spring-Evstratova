package ru.otus.project.achievement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAchievementRequest {

    @Min(1)
    private long gameId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean isHidden;
}
