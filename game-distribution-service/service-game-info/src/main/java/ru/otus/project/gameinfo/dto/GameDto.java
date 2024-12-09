package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.gameinfo.models.Game;

import java.util.List;

@AllArgsConstructor
@Data
public class GameDto {

    private long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private DeveloperDto developer;

    private List<GenreDto> genres;

    private SystemRequirementDto minimumSystemRequirement;

    private SystemRequirementDto recommendedSystemRequirement;

    public static GameDto toDto(Game game) {
        List<GenreDto> genres = game.getGenres().stream().map(GenreDto::toDto).toList();
        DeveloperDto developer = DeveloperDto.toDto(game.getDeveloper());

        SystemRequirementDto minimumSystemRequirementDto = null;
        if (game.getMinimumSystemRequirement() != null) {
            minimumSystemRequirementDto = SystemRequirementDto.toDto(game.getMinimumSystemRequirement());
        }

        SystemRequirementDto recommendedSystemRequirementDto = null;
        if (game.getRecommendedSystemRequirement() != null) {
            recommendedSystemRequirementDto = SystemRequirementDto.toDto(game.getRecommendedSystemRequirement());
        }
        return new GameDto(game.getId(), game.getTitle(), game.getDescription(), developer, genres,
                minimumSystemRequirementDto, recommendedSystemRequirementDto);
    }
}
