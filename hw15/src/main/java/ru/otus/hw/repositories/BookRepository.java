package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Book;
import ru.otus.hw.projections.CustomBook;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "book", excerptProjection = CustomBook.class)
public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(value = "book-author-entity-graph")
    Optional<Book> findById(long id);

    @EntityGraph(value = "book-author-entity-graph")
    List<Book> findAll();

    @RestResource(path = "titles", rel = "titles")
    Optional<Book> findByTitle(String title);
}
