package ru.otus.project.order.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ru.otus.project.order.dto.OrderDto;
import ru.otus.project.order.exceptions.EntityNotFoundException;
import ru.otus.project.order.exceptions.OrderAlreadyExistsException;
import ru.otus.project.order.models.Order;
import ru.otus.project.order.models.GameProduct;
import ru.otus.project.order.repositories.OrderRepository;
import ru.otus.project.order.repositories.GameProductRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final GameProductRepository gameProductRepository;

    @Override
    public Optional<OrderDto> findOrderByUserIdAndGameId(long userId, long gameId) {
        return orderRepository.findByUserIdAndGameProductGameId(userId, gameId).map(OrderDto::toDto);
    }

    @Override
    public List<OrderDto> findAllOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId).stream().map(OrderDto::toDto).toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Override
    public OrderDto insert(long userId, long gameId) {
        GameProduct gameProduct = gameProductRepository.findById(gameId).orElseThrow(
                () -> new EntityNotFoundException("Game Product with game_id %s not found".formatted(gameId)));

        if (orderRepository.existsByUserIdAndGameProduct(userId, gameProduct)) {
            throw new OrderAlreadyExistsException("Order for game_id %s by user_id %s already exist"
                    .formatted(userId, gameId));
        }

        return OrderDto.toDto(orderRepository.save(new Order(gameProduct, userId, true)));
    }
}
