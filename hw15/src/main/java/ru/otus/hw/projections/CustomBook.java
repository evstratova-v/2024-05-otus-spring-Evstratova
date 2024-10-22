package ru.otus.hw.projections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

@Projection(name = "customBook", types = Book.class)
public interface CustomBook {

    String getTitle();

    @Value("#{target.getAuthor().getFullName()}")
    String getAuthor();

    List<Genre> getGenres();
}
