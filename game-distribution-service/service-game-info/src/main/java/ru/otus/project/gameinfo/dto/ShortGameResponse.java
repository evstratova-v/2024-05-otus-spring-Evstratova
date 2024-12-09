package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.gameinfo.projections.ShortGameProjection;

import java.util.List;

@AllArgsConstructor
@Data
public class ShortGameResponse {

    private long id;

    @NotBlank
    private String title;

    @NotNull
    private DeveloperDto developer;

    private List<GenreDto> genres;

    public static ShortGameResponse toDto(ShortGameProjection game) {
        List<GenreDto> genres = game.getGenres().stream().map(GenreDto::toDto).toList();
        DeveloperDto developer = DeveloperDto.toDto(game.getDeveloper());
        return new ShortGameResponse(game.getId(), game.getTitle(), developer, genres);
    }
}
