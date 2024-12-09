package ru.otus.project.achievement.rest;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.AchievementEventDto;
import ru.otus.project.achievement.exceptions.AchievementEventAlreadyExistException;
import ru.otus.project.achievement.exceptions.AchievementNotFoundException;
import ru.otus.project.achievement.services.AchievementHistoryService;
import ru.otus.project.achievement.services.AchievementService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AchievementHistoryController {

    private final AchievementHistoryService achievementHistoryService;

    private final AchievementService achievementService;

    private final OrderServiceClient orderServiceClient;

    @Operation(summary = "Get achievement history for user", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/v1/achievement-history")
    public List<AchievementEventDto> getAchievementHistoryByUserId(@AuthenticationPrincipal Jwt principal) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        return achievementHistoryService.findAchievementHistoryByUserId(userId);
    }

    @Operation(summary = "Get achievement history for user by game id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/api/v1/achievement-history/{game_id}")
    public List<AchievementEventDto> getAchievementHistoryByUserIdAndGameId(@AuthenticationPrincipal Jwt principal,
                                                                            @PathVariable("game_id") long gameId) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        return achievementHistoryService.findAchievementHistoryByUserIdAndGameId(userId, gameId);
    }

    @Operation(summary = "Add new achievement event for user", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/achievement-history")
    public AchievementEventDto addAchievementEvent(@AuthenticationPrincipal Jwt principal,
                                                   @RequestParam("achievement_id") long achievementId
    ) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        AchievementDto achievementDto = achievementService.findById(achievementId).orElseThrow(
                () -> new AchievementNotFoundException("Achievement with id %s not found".formatted(achievementId))
        );
        orderServiceClient.checkGamePaid(achievementDto.getGameId(), userId);

        return achievementHistoryService.insert(userId, achievementDto);
    }

    @ExceptionHandler({AchievementEventAlreadyExistException.class, AchievementNotFoundException.class,
            FeignException.class})
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
