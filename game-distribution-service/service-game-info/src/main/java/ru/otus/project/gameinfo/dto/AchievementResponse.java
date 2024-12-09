package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementResponse {

    private List<AchievementDto> achievements;

    @Min(0)
    private int countOfHiddenAchievements;
}
