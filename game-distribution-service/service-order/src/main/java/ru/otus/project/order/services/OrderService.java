package ru.otus.project.order.services;

import ru.otus.project.order.dto.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Optional<OrderDto> findOrderByUserIdAndGameId(long userId, long gameId);

    List<OrderDto> findAllOrdersByUserId(long userId);

    OrderDto insert(long userId, long gameId);
}
