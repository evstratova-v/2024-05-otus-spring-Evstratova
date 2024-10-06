package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.models.Book;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortBookDto {

    private long id;

    @NotBlank(message = "Title field should not be blank")
    @Size(min = 2, max = 50, message = "Title field should be between 2 and 50 characters")
    private String title;

    private long authorId;

    @NotNull(message = "At least one genre should be selected")
    private List<Long> genresIds;

    public static ShortBookDto toDto(Book book) {
        List<GenreDto> genres = book.getGenres().stream().map(GenreDto::toDto).toList();
        return new ShortBookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                genres.stream().map(GenreDto::getId).toList()
        );
    }
}
