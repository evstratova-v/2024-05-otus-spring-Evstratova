package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GenreRepository для работы с жанрами ")
@DataMongoTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = mongoTemplate.findAll(Genre.class);
    }

    @DisplayName("должен возвращать список жанров по нескольким id")
    @Test
    void shouldReturnCorrectGenreById() {
        var actualGenres = genreRepository.findAllById(Set.of(
                dbGenres.get(0).getId(),
                dbGenres.get(4).getId(),
                dbGenres.get(5).getId()));
        var expectedGenres = List.of(dbGenres.get(0), dbGenres.get(4), dbGenres.get(5));

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }

    @DisplayName("должен возвращать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var actualGenres = genreRepository.findAll();
        var expectedGenres = dbGenres;

        assertThat(actualGenres).containsExactlyElementsOf(expectedGenres);
    }
}
