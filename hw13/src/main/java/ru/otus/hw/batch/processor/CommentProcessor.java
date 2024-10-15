package ru.otus.hw.batch.processor;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.models.mongo.MongoBook;
import ru.otus.hw.models.mongo.MongoComment;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CommentProcessor implements ItemProcessor<Comment, MongoComment> {

    private final CacheManager cacheManager;

    @Override
    public MongoComment process(Comment comment) {
        MongoComment mongoComment = getMongoCommentAndSaveBookIdInCache(comment);
        saveMongoCommentInCache(mongoComment);

        return mongoComment;
    }

    private MongoComment getMongoCommentAndSaveBookIdInCache(Comment comment) {
        Long jpaBookId = comment.getBook().getId();
        MongoBook mongoBook = new MongoBook();

        Cache cache = cacheManager.getCache("jpaBookId_mongoBookId");

        if (cache.get(jpaBookId) == null) {
            String mongoBookId = new ObjectId().toHexString();
            mongoBook.setId(mongoBookId);

            cache.put(jpaBookId, mongoBookId);
        } else {
            String mongoBookId = cache.get(jpaBookId, String.class);
            mongoBook.setId(mongoBookId);
        }

        return new MongoComment(new ObjectId().toHexString(), comment.getText(), mongoBook);
    }

    private void saveMongoCommentInCache(MongoComment mongoComment) {
        String mongoBookId = mongoComment.getBook().getId();
        Cache cache = cacheManager.getCache("mongoBookId_mongoComments");

        if (cache.get(mongoBookId) == null) {
            cache.put(mongoBookId, new ArrayList<>(List.of(mongoComment)));
        } else {
            List<MongoComment> comments = cache.get(mongoBookId, List.class);
            comments.add(mongoComment);
        }
    }
}
