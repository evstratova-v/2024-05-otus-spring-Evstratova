package ru.otus.project.order.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ru.otus.project.order.dto.GameProductDto;
import ru.otus.project.order.exceptions.EntityNotFoundException;
import ru.otus.project.order.services.GameProductService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GameProductController {

    private final GameProductService gameProductService;

    @Operation(summary = "Get product by game_id", description = "Returns GameProductDto")
    @GetMapping("/api/v1/product/{game_id}")
    public GameProductDto getGameProductById(@PathVariable("game_id") long gameId) {
        return gameProductService.findByGameId(gameId).orElseThrow(
                () -> new EntityNotFoundException("Game product with game_id %s not found".formatted(gameId))
        );
    }

    @Operation(summary = "Get price by game_id", description = "Returns price")
    @GetMapping("/api/v1/product/{game_id}/price")
    public Long getGameProductPriceById(@PathVariable("game_id") long gameId) {
        return gameProductService.findPriceByGameId(gameId).orElseThrow(
                () -> new EntityNotFoundException("Game product with game_id %s not found".formatted(gameId))
        );
    }

    @Operation(summary = "Get products by multiple game_id", description = "Returns list of GameProductDto")
    @GetMapping("/api/v1/product")
    public List<GameProductDto> getAllGameProductsByIds(@RequestParam("game_ids") List<Long> gameIds) {
        return gameProductService.findAllByGameIds(gameIds);
    }

    @Operation(summary = "Add new game product", description = "Returns GameProductDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/product")
    public GameProductDto addGameProduct(@RequestBody @Valid GameProductDto gameProductDto) {
        return gameProductService.insert(gameProductDto);
    }

    @Operation(summary = "Edit game product", description = "Returns GameProductDto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/v1/product")
    public GameProductDto editGameProduct(@RequestBody @Valid GameProductDto gameProductDto) {
        return gameProductService.update(gameProductDto);
    }

    @Operation(summary = "Delete game product by game_id", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/v1/product/{game_id}")
    public void deleteGameProduct(@PathVariable("game_id") long gameId) {
        gameProductService.deleteById(gameId);
    }

    @Operation(summary = "Get game ids paid by user", description = "Returns list of game ids")
    @GetMapping("/api/v1/product/ordered")
    public List<Long> getOrderedGameIds(@RequestParam("user_id") long userId) {
        return gameProductService.findAllPaidGameIdsByUserId(userId);
    }

    @Operation(summary = "Check game id paid by user", description = "Returns ok if game was paid")
    @GetMapping("/api/v1/product/ordered/{game_id}")
    public ResponseEntity<?> getOrderedGameIds(@PathVariable("game_id") long gameId,
                                               @RequestParam("user_id") long userId) {
        if (gameProductService.existsPaidGameIdByUserId(userId, gameId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
