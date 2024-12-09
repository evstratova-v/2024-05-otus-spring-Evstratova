package ru.otus.project.gameinfo.rest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("order-service")
public interface GamePriceClient {

    @CircuitBreaker(name = "getPrice")
    @GetMapping("/api/v1/product/{game_id}/price")
    long getPrice(@PathVariable("game_id") long gameId);
}
