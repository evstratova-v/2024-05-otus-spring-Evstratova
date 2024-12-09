package ru.otus.project.order.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.project.order.dto.GameProductDto;
import ru.otus.project.order.exceptions.EntityNotFoundException;
import ru.otus.project.order.exceptions.GameProductAlreadyExistException;
import ru.otus.project.order.models.GameProduct;
import ru.otus.project.order.repositories.GameProductRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GameProductServiceImpl implements GameProductService {

    private final GameProductRepository gameProductRepository;

    @Override
    public Optional<GameProductDto> findByGameId(long gameId) {
        return gameProductRepository.findById(gameId).map(GameProductDto::toDto);
    }

    @Override
    public Optional<Long> findPriceByGameId(long gameId) {
        return gameProductRepository.findPriceByGameId(gameId);
    }

    @Override
    public List<GameProductDto> findAllByGameIds(List<Long> gameIds) {
        return gameProductRepository.findAllById(gameIds).stream().map(GameProductDto::toDto).toList();
    }

    @Override
    public List<Long> findAllPaidGameIdsByUserId(long userId) {
        return gameProductRepository.findAllPaidGameIdsByUserId(userId);
    }

    @Override
    public boolean existsPaidGameIdByUserId(long userId, long gameId) {
        return gameProductRepository.existsPaidGameIdByUserId(userId, gameId);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Override
    public GameProductDto insert(GameProductDto gameProductDto) {
        long gameId = gameProductDto.getGameId();
        if (gameProductRepository.existsById(gameId)) {
            throw new GameProductAlreadyExistException("Game product with id %s already exist".formatted(gameId));
        }

        GameProduct gameProduct = gameProductRepository.save(new GameProduct(
                gameProductDto.getGameId(), gameProductDto.getPrice()));
        return GameProductDto.toDto(gameProduct);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Override
    public GameProductDto update(GameProductDto gameProductDto) {
        long gameId = gameProductDto.getGameId();
        Optional<GameProduct> gameProductOptional = gameProductRepository.findById(gameId);
        if (gameProductOptional.isEmpty()) {
            throw new EntityNotFoundException("Game product with game_id %s not found".formatted(gameId));
        }
        GameProduct gameProduct = gameProductOptional.get();
        gameProduct.setPrice(gameProductDto.getPrice());
        return GameProductDto.toDto(gameProductRepository.save(gameProduct));
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Override
    public void deleteById(long gameId) {
        gameProductRepository.deleteById(gameId);
    }
}
