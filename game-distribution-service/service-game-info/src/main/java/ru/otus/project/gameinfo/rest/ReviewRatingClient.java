package ru.otus.project.gameinfo.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("review-service")
public interface ReviewRatingClient {

    @GetMapping("/api/v1/review/rating")
    @CircuitBreaker(name = "getRating")
    String getRating(@RequestParam("game_id") long gameId);
}
