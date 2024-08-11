package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books")
public class BookWithoutComments {

    @Id
    private String id;

    private String title;

    private Author author;

    private List<Genre> genres;
}
