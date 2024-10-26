package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.hw.models.Comment;

import java.util.List;

@RepositoryRestResource(path = "comment")
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @RestResource(path = "bookTitles", rel = "bookTitles")
    List<Comment> findByBookTitle(String bookTitle);

    @RestResource(exported = false)
    List<Comment> findByBookId(long bookId);
}
