package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id).map(CommentDto::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId).stream().map(CommentDto::toDto).toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String text, long bookId) {
        return CommentDto.toDto(save(0L, text, bookId));
    }

    @Transactional
    @Override
    public CommentDto update(long id, String text, long bookId) {
        return CommentDto.toDto(save(id, text, bookId));
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        Comment comment = new Comment(id, text, book);
        return commentRepository.save(comment);
    }
}
