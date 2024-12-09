package ru.otus.project.gameinfo.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.project.gameinfo.dto.AchievementResponse;

@FeignClient("achievement-service")
public interface GameAchievementClient {

    @CircuitBreaker(name = "getAchievements")
    @GetMapping("/api/v1/achievement")
    AchievementResponse getAchievements(@RequestParam("game_id") long gameId);
}
