package ru.otus.hw.repositories;

import ru.otus.hw.models.BookWithoutComments;
import ru.otus.hw.models.Comment;

public interface BookRepositoryCustom {
    void removeCommentsArrayElementById(String id);

    void addCommentsArrayElementById(Comment comment);

    BookWithoutComments saveIgnoreComments(BookWithoutComments bookWithoutComments);

    void deleteByIdWithComments(String id);
}
