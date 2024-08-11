package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String>, BookRepositoryCustom {
    List<BookWithoutComments> findAllWithoutCommentsBy();

    Optional<BookWithoutComments> findWithoutCommentsById(String id);
}
