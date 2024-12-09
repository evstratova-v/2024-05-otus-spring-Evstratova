package ru.otus.project.review.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.project.review.models.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByGameId(long gameId);

    boolean existsByGameIdAndUserId(long gameId, long userId);

    boolean existsByGameId(long gameId);

    int countByRecommendedIsTrueAndGameId(long gameId);

    int countByRecommendedIsFalseAndGameId(long gameId);
}
