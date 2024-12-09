package ru.otus.project.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.project.order.models.GameProduct;

import java.util.List;
import java.util.Optional;

public interface GameProductRepository extends JpaRepository<GameProduct, Long> {

    @Query("select p.gameId from GameProduct p where p in (select o.gameProduct from Order o where " +
            "o.userId = :user_id and o.paid = true)")
    List<Long> findAllPaidGameIdsByUserId(@Param("user_id") long userId);

    @Query("select case when count(p)> 0 then true else false end from GameProduct p where p in " +
            "(select o.gameProduct from Order o where o.userId = :user_id and o.paid = true) and p.gameId = :game_id")
    boolean existsPaidGameIdByUserId(@Param("user_id") long userId, @Param("game_id") long gameId);

    @Query("select p.price from GameProduct p where p.gameId = :game_id")
    Optional<Long> findPriceByGameId(@Param("game_id") long gameId);
}
