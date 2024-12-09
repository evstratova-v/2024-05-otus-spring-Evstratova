package ru.otus.project.gameinfo.projections;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.otus.project.gameinfo.models.Developer;
import ru.otus.project.gameinfo.models.Genre;

import java.util.List;

public interface ShortGameProjection {

    long getId();

    @NotBlank
    String getTitle();

    @NotNull
    Developer getDeveloper();

    List<Genre> getGenres();
}
