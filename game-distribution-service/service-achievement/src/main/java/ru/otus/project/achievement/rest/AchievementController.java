package ru.otus.project.achievement.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.AchievementResponse;
import ru.otus.project.achievement.dto.CreateAchievementRequest;
import ru.otus.project.achievement.services.AchievementService;

@RequiredArgsConstructor
@RestController
public class AchievementController {

    private final AchievementService achievementService;

    @Operation(summary = "Get achievements by game id")
    @GetMapping("/api/v1/achievement")
    public AchievementResponse getAchievementsByGameId(@RequestParam("game_id") long gameId) {
        return new AchievementResponse(achievementService.findAllByGameIdAndNotHidden(gameId),
                achievementService.countByGameIdIsHiddenTrue(gameId));
    }

    @Operation(summary = "Add new achievement", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/achievement")
    public AchievementDto addAchievement(@RequestBody @Valid CreateAchievementRequest createAchievementRequest) {
        return achievementService.insert(createAchievementRequest);
    }

    @Operation(summary = "Edit achievement", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/v1/achievement")
    public AchievementDto editAchievement(@RequestBody @Valid AchievementDto achievementDto) {
        return achievementService.update(achievementDto);
    }

    @Operation(summary = "Delete achievement by id", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = {"/api/v1/achievement/{achievement_id}"})
    public void deleteAchievement(@PathVariable("achievement_id") long achievementId) {
        achievementService.deleteById(achievementId);
    }

    @Operation(summary = "Delete achievements by game id", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/v1/achievement")
    public void deleteAchievementByGameId(@RequestParam("game_id") long gameId) {
        achievementService.deleteByGameId(gameId);
    }
}
