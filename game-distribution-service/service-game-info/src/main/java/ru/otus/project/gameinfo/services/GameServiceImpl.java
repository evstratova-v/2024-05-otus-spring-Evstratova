package ru.otus.project.gameinfo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.project.gameinfo.dto.GameDto;
import ru.otus.project.gameinfo.dto.GameRequest;
import ru.otus.project.gameinfo.dto.ShortGameResponse;
import ru.otus.project.gameinfo.dto.SystemRequirementDto;
import ru.otus.project.gameinfo.exceptions.EntityNotFoundException;
import ru.otus.project.gameinfo.models.Developer;
import ru.otus.project.gameinfo.models.Game;
import ru.otus.project.gameinfo.models.Genre;
import ru.otus.project.gameinfo.models.SystemRequirement;
import ru.otus.project.gameinfo.repositories.DeveloperRepository;
import ru.otus.project.gameinfo.repositories.GameRepository;
import ru.otus.project.gameinfo.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;

    private final DeveloperRepository developerRepository;

    private final GenreRepository genreRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ShortGameResponse> findAll() {
        return gameRepository.findAllProjectedBy().stream().map(ShortGameResponse::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShortGameResponse> findAllByGenresIds(List<Long> genres) {
        return gameRepository.findAllByGenresIdIn(genres).stream().map(ShortGameResponse::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShortGameResponse> findAllByTitlePattern(String pattern) {
        return gameRepository.findAllByTitleContaining(pattern).stream().map(ShortGameResponse::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShortGameResponse> findAllByGenresAndPattern(List<Long> genresIds, String pattern) {
        return gameRepository.findAllByTitleContainingAndGenresIdIn(pattern, genresIds).stream()
                .map(ShortGameResponse::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<GameDto> findById(long id) {
        return gameRepository.findById(id).map(GameDto::toDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Transactional
    @Override
    public GameDto insert(GameRequest gameRequest) {
        return GameDto.toDto(save(gameRequest));
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Transactional
    @Override
    public GameDto update(GameRequest gameRequest) {
        return GameDto.toDto(save(gameRequest));
    }

    private Game save(GameRequest gameRequest) {
        long developerId = gameRequest.getDeveloperId();
        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new EntityNotFoundException("Developer with id %d not found".formatted(developerId)));

        List<Long> genresIds = gameRequest.getGenresIds();
        List<Genre> genres = genreRepository.findAllById(genresIds);

        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        Game game = new Game(gameRequest.getId(), gameRequest.getTitle(), gameRequest.getDescription(), developer,
                genres);
        mappingSystemRequirements(gameRequest, game);

        return gameRepository.save(game);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Transactional
    @Override
    public void deleteById(long id) {
        gameRepository.deleteById(id);
    }

    private void mappingSystemRequirements(GameRequest gameRequest, Game game) {
        if (gameRequest.getMinimumSystemRequirement() != null) {
            SystemRequirement minimumSystemRequirement = SystemRequirementDto.toDomainObject(
                    gameRequest.getMinimumSystemRequirement());
            game.setMinimumSystemRequirement(minimumSystemRequirement);
        }
        if (gameRequest.getRecommendedSystemRequirement() != null) {
            SystemRequirement recommendedSystemRequirement = SystemRequirementDto.toDomainObject(
                    gameRequest.getRecommendedSystemRequirement());
            game.setRecommendedSystemRequirement(recommendedSystemRequirement);
        }
    }
}
