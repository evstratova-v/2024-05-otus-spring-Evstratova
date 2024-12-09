package ru.otus.project.review.rest;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.project.review.dto.CreateReviewRequest;
import ru.otus.project.review.dto.ReviewDto;
import ru.otus.project.review.dto.UpdateReviewRequest;
import ru.otus.project.review.exceptions.EntityNotFoundException;
import ru.otus.project.review.exceptions.ReviewAlreadyExistsException;
import ru.otus.project.review.services.ReviewService;

import java.text.DecimalFormat;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    private final OrderService orderService;

    @Operation(summary = "Find all reviews by game id", description = "Returns list of ReviewDto")
    @GetMapping("/api/v1/review")
    public List<ReviewDto> findReviews(@RequestParam("game_id") long gameId) {
        return reviewService.findByGameId(gameId);
    }

    @Operation(summary = "Add new review", description = "Add new review by CreateReviewRequest",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/api/v1/review")
    public ReviewDto addReview(@AuthenticationPrincipal Jwt principal,
                               @RequestBody @Valid CreateReviewRequest createReviewRequest) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        long gameId = createReviewRequest.getGameId();

        orderService.checkGamePaid(gameId, userId);

        return reviewService.insert(userId, createReviewRequest);
    }

    @Operation(summary = "Edit review", description = "Edit review by UpdateReviewRequest",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/api/v1/review")
    public ReviewDto editReview(@AuthenticationPrincipal Jwt principal,
                                @RequestBody @Valid UpdateReviewRequest updateReviewRequest) {
        long userId = Long.parseLong(principal.getClaims().get("userId").toString());
        return reviewService.update(userId, updateReviewRequest);
    }

    @Operation(summary = "Delete review", description = "Delete review by review id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/api/v1/review/{review_id}")
    public void deleteReview(@PathVariable("review_id") long reviewId) {
        reviewService.deleteById(reviewId);
    }

    @Operation(summary = "Get rating by game id", description = "Returns rating of game")
    @GetMapping("/api/v1/review/rating")
    public String getRating(@RequestParam("game_id") long gameId) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        double rating = reviewService.getRating(gameId);
        return decimalFormat.format(rating);
    }

    @ExceptionHandler({EntityNotFoundException.class, ReviewAlreadyExistsException.class, FeignException.class})
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
