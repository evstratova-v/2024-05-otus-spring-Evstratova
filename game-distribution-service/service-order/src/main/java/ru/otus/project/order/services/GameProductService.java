package ru.otus.project.order.services;

import ru.otus.project.order.dto.GameProductDto;

import java.util.List;
import java.util.Optional;

public interface GameProductService {
    Optional<GameProductDto> findByGameId(long gameId);

    Optional<Long> findPriceByGameId(long gameId);

    List<GameProductDto> findAllByGameIds(List<Long> gameIds);

    List<Long> findAllPaidGameIdsByUserId(long userId);

    boolean existsPaidGameIdByUserId(long userId, long gameId);

    GameProductDto insert(GameProductDto gameProductDto);

    GameProductDto update(GameProductDto gameProductDto);

    void deleteById(long gameId);
}
