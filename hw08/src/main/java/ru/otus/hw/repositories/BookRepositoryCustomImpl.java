package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;


@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public void removeCommentsArrayElementById(String id) {
        val query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        val update = new Update().pull("comments", query);
        mongoTemplate.updateMulti(new Query(), update, Book.class);
    }

    @Override
    public void addCommentsArrayElementById(Comment comment) {
        String bookId = comment.getBook().getId();
        val query = Query.query(Criteria.where("_id").is(new ObjectId(bookId)));
        val update = new Update().addToSet("comments", comment);
        mongoTemplate.updateMulti(query, update, Book.class);
    }
}
