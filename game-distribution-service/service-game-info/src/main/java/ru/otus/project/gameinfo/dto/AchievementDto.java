package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AchievementDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private boolean isHidden;
}
