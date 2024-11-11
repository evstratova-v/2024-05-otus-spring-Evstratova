package ru.otus.hw.repositories;

import com.mongodb.DBRef;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Comment;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<BookWithoutComments> saveIgnoreComments(BookWithoutComments bookWithoutComments) {
        ObjectId objectId = new ObjectId(bookWithoutComments.getId());
        val query = Query.query(Criteria.where("_id").is(objectId));
        val update = new Update().set("title", bookWithoutComments.getTitle())
                .set("author", bookWithoutComments.getAuthor())
                .set("genres", bookWithoutComments.getGenres());

        return mongoTemplate.updateFirst(query, update, Book.class)
                .then(mongoTemplate.findById(objectId, BookWithoutComments.class));
    }

    @Override
    public Mono<Void> deleteByIdWithComments(String id) {
        ObjectId objectId = new ObjectId(id);
        DBRef dbRef = new DBRef("books", objectId);
        val queryDeleteBook = Query.query(Criteria.where("_id").is(objectId));
        val queryDeleteComments = Query.query(Criteria.where("book").is(dbRef));

        return mongoTemplate.remove(queryDeleteBook, Book.class)
                .then(mongoTemplate.remove(queryDeleteComments, Comment.class))
                .then();
    }
}
