package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Author;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AuthorRepository для работы с авторами ")
@DataMongoTest
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = mongoTemplate.findAll(Author.class);
    }

    @DisplayName("должен возвращать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        authors.forEach(author -> {
            var actualAuthor = authorRepository.findById(author.getId());
            assertThat(actualAuthor).isPresent()
                    .get()
                    .isEqualTo(author);
        });
    }

    @DisplayName("должен возвращать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorsList() {
        var actualAuthors = authorRepository.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(authors);
    }
}
