package ru.otus.project.review.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/product/ordered/{game_id}")
    @CircuitBreaker(name = "checkGamePaid")
    void checkGamePaid(@PathVariable("game_id") long gameId, @RequestParam(name = "user_id") long userId);
}
