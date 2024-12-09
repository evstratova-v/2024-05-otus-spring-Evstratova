package ru.otus.project.review.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.otus.project.review.dto.CreateReviewRequest;
import ru.otus.project.review.dto.ReviewDto;
import ru.otus.project.review.dto.UpdateReviewRequest;
import ru.otus.project.review.exceptions.EntityNotFoundException;
import ru.otus.project.review.exceptions.ReviewAlreadyExistsException;
import ru.otus.project.review.models.Review;
import ru.otus.project.review.repositories.ReviewRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewDto> findByGameId(long gameId) {
        return reviewRepository.findByGameId(gameId).stream().map(ReviewDto::toDto).toList();
    }

    @Override
    public ReviewDto insert(long userId, CreateReviewRequest createReviewRequest) {
        long gameId = createReviewRequest.getGameId();
        if (reviewRepository.existsByGameIdAndUserId(gameId, userId)) {
            throw new ReviewAlreadyExistsException("User with id %s is already add review for game with id %s"
                    .formatted(userId, gameId));
        }
        Review review = reviewRepository.save(new Review(gameId,
                createReviewRequest.isRecommended(),
                createReviewRequest.getComment()));
        return ReviewDto.toDto(review);
    }

    @Override
    public ReviewDto update(long userId, UpdateReviewRequest updateReviewRequest) {
        long reviewId = updateReviewRequest.getId();
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new EntityNotFoundException("Review with id %s not found".formatted(reviewId))
        );
        if (review.getUserId() != userId) {
            throw new AccessDeniedException("User with id %s can't edit review with id %s".formatted(userId, reviewId));
        }
        review.setRecommended(updateReviewRequest.isRecommended());
        review.setComment(updateReviewRequest.getComment());
        review = reviewRepository.save(review);

        return ReviewDto.toDto(review);
    }

    @Override
    public void deleteById(long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public double getRating(long gameId) {
        if (!reviewRepository.existsByGameId(gameId)) {
            return 0;
        }
        int notRecommended = reviewRepository.countByRecommendedIsFalseAndGameId(gameId);
        int recommended = reviewRepository.countByRecommendedIsTrueAndGameId(gameId);
        int sum = notRecommended + recommended;

        return (double) recommended / sum;
    }
}
