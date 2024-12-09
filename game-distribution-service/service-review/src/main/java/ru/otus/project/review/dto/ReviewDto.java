package ru.otus.project.review.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.review.models.Review;

import java.time.LocalDateTime;
import java.time.ZoneId;

@AllArgsConstructor
@Data
public class ReviewDto {

    @Min(1)
    private long id;

    @Min(1)
    private long gameId;

    private boolean recommended;

    @Size(min = 1, max = 3000)
    private String comment;

    @NotNull
    private LocalDateTime created;

    @NotNull
    private LocalDateTime updated;

    @Min(1)
    private long userId;

    public static ReviewDto toDto(Review review) {
        return new ReviewDto(review.getId(),
                review.getGameId(),
                review.isRecommended(),
                review.getComment(),
                review.getCreated().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                review.getUpdated().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),
                review.getUserId());
    }
}
