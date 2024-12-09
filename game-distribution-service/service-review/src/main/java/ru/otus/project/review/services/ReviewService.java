package ru.otus.project.review.services;

import ru.otus.project.review.dto.ReviewDto;
import ru.otus.project.review.dto.CreateReviewRequest;
import ru.otus.project.review.dto.UpdateReviewRequest;

import java.util.List;

public interface ReviewService {
    List<ReviewDto> findByGameId(long gameId);

    ReviewDto insert(long userId, CreateReviewRequest createReviewRequest);

    ReviewDto update(long userId, UpdateReviewRequest updateReviewRequest);

    void deleteById(long id);

    double getRating(long gameId);
}
