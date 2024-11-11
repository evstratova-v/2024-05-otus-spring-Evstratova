package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.hw.models.Author;

@Data
@AllArgsConstructor
public class AuthorDto {

    private String id;

    private String fullName;

    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}
