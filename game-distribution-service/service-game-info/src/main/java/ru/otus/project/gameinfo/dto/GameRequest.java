package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GameRequest {

    private long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private long developerId;

    @NotEmpty
    private List<Long> genresIds;

    private SystemRequirementDto minimumSystemRequirement;

    private SystemRequirementDto recommendedSystemRequirement;
}
