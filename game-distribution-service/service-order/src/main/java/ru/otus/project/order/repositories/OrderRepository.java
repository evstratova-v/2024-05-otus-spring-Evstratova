package ru.otus.project.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.order.models.Order;
import ru.otus.project.order.models.GameProduct;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUserIdAndGameProductGameId(long userId, long gameId);

    List<Order> findByUserId(long userId);

    boolean existsByUserIdAndGameProduct(long userId, GameProduct gameId);
}
