package ru.otus.hw.repositories;

import com.mongodb.DBRef;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookWithoutComments;
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

    @Override
    public BookWithoutComments saveIgnoreComments(BookWithoutComments bookWithoutComments) {
        ObjectId objectId = new ObjectId(bookWithoutComments.getId());
        val query = Query.query(Criteria.where("_id").is(objectId));
        val update = new Update().set("title", bookWithoutComments.getTitle())
                .set("author", bookWithoutComments.getAuthor())
                .set("genres", bookWithoutComments.getGenres());
        mongoTemplate.updateFirst(query, update, Book.class);
        return mongoTemplate.findById(objectId, BookWithoutComments.class);
    }

    @Override
    public void deleteByIdWithComments(String id) {
        ObjectId objectId = new ObjectId(id);
        DBRef dbRef = new DBRef("books", objectId);
        val queryDeleteBook = Query.query(Criteria.where("_id").is(objectId));
        val queryDeleteComments = Query.query(Criteria.where("book").is(dbRef));

        mongoTemplate.remove(queryDeleteBook, Book.class);
        mongoTemplate.remove(queryDeleteComments, Comment.class);
    }
}
