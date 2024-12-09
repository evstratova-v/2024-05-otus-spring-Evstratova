package ru.otus.project.review.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateReviewRequest {

    @Min(1)
    private long gameId;

    private boolean recommended;

    @NotBlank
    @Size(min = 1, max = 3000)
    private String comment;
}
