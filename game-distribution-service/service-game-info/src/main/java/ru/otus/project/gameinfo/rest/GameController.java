package ru.otus.project.gameinfo.rest;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.gameinfo.dto.AchievementResponse;
import ru.otus.project.gameinfo.dto.GameDto;
import ru.otus.project.gameinfo.dto.GameInfoResponse;
import ru.otus.project.gameinfo.dto.GameRequest;
import ru.otus.project.gameinfo.dto.ShortGameResponse;
import ru.otus.project.gameinfo.exceptions.EntityNotFoundException;
import ru.otus.project.gameinfo.services.GameService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class GameController {

    private final GameService gameService;

    private final GameAchievementClient gameAchievementClient;

    private final GamePriceClient gamePriceClient;

    private final ReviewRatingClient reviewRatingClient;

    @Operation(summary = "Get games by optional params", description = "Returns list of ShortGameResponse")
    @GetMapping("/api/v1/game")
    public List<ShortGameResponse> getAllGames(@RequestParam(name = "genres", required = false) List<Long> genresIds,
                                               @RequestParam(name = "title", required = false) String pattern) {
        if (genresIds != null && pattern != null) {
            return gameService.findAllByGenresAndPattern(genresIds, pattern);
        }
        if (genresIds != null) {
            return gameService.findAllByGenresIds(genresIds);
        }
        if (pattern != null) {
            return gameService.findAllByTitlePattern(pattern);
        }
        return gameService.findAll();
    }

    @Operation(summary = "Get game info by id", description = "Returns GameInfoResponse")
    @GetMapping("/api/v1/game/{game_id}")
    public GameInfoResponse getGame(@PathVariable("game_id") long gameId) {
        AchievementResponse achievementResponse = gameAchievementClient.getAchievements(gameId);
        long price = gamePriceClient.getPrice(gameId);
        GameDto gameDto = gameService.findById(gameId)
                .orElseThrow(() -> new EntityNotFoundException("Game with id %s not found".formatted(gameId)));
        String rating = reviewRatingClient.getRating(gameId);

        return new GameInfoResponse(gameDto, achievementResponse, price, rating);
    }

    @Operation(summary = "Add new game", description = "Add new game by GameRequest, returns GameDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/game")
    public GameDto addGame(@RequestBody @Valid GameRequest gameRequest) {
        return gameService.insert(gameRequest);
    }

    @Operation(summary = "Edit game", description = "Edit game by GameRequest, returns GameDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/v1/game")
    public GameDto editGame(@RequestBody @Valid GameRequest gameRequest) {
        return gameService.update(gameRequest);
    }

    @Operation(summary = "Delete game by id", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/v1/game/{game_id}")
    public void deleteGame(@PathVariable("game_id") long gameId) {
        gameService.deleteById(gameId);
    }

    @ExceptionHandler({EntityNotFoundException.class, FeignException.class})
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
