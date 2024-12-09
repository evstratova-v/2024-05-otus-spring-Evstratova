package ru.otus.project.gameinfo.services;

import ru.otus.project.gameinfo.dto.GameDto;
import ru.otus.project.gameinfo.dto.GameRequest;
import ru.otus.project.gameinfo.dto.ShortGameResponse;

import java.util.List;
import java.util.Optional;

public interface GameService {

    List<ShortGameResponse> findAll();

    List<ShortGameResponse> findAllByGenresIds(List<Long> genresIds);

    List<ShortGameResponse> findAllByTitlePattern(String pattern);

    List<ShortGameResponse> findAllByGenresAndPattern(List<Long> genresIds, String pattern);

    Optional<GameDto> findById(long id);

    GameDto insert(GameRequest gameRequest);

    GameDto update(GameRequest gameRequest);

    void deleteById(long id);
}
