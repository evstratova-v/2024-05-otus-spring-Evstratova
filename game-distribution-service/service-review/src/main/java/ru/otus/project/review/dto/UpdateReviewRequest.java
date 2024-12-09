package ru.otus.project.review.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateReviewRequest {

    @Min(1)
    private long id;

    private boolean recommended;

    @Size(min = 1, max = 3000)
    private String comment;
}
