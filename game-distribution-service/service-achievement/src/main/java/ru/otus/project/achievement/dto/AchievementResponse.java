package ru.otus.project.achievement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AchievementResponse {
    private List<AchievementDto> achievements;

    private int countOfHiddenAchievements;
}
