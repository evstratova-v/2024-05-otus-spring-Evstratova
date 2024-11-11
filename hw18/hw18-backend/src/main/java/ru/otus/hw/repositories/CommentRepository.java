package ru.otus.hw.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.hw.models.Comment;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
}
