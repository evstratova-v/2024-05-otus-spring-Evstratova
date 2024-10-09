package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ShortBookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.security.AclServiceWrapperService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final AclServiceWrapperService aclServiceWrapperService;

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN') || hasPermission(#id, 'ru.otus.hw.models.Book', 'READ')")
    @Override
    public Optional<ShortBookDto> findShortBookById(long id) {
        return bookRepository.findById(id).map(ShortBookDto::toDto);
    }

    @Transactional(readOnly = true)
    @PostFilter("hasRole('ADMIN') or hasPermission(filterObject.getId(), 'ru.otus.hw.models.Book', 'READ')")
    @Override
    public List<BookDto> findAll() {
        return new ArrayList<>(bookRepository.findAll().stream().map(BookDto::toDto).toList());
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @Override
    public BookDto insert(ShortBookDto shortBookDto) {
        return BookDto.toDto(save(shortBookDto));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') || hasPermission(#shortBookDto.getId(), 'ru.otus.hw.models.Book', 'WRITE')")
    @Override
    public BookDto update(ShortBookDto shortBookDto) {
        return BookDto.toDto(save(shortBookDto));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Book save(ShortBookDto shortBookDto) {
        long authorId = shortBookDto.getAuthorId();
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));

        var genresIds = shortBookDto.getGenresIds();
        var genres = genreRepository.findAllById(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(shortBookDto.getId(), shortBookDto.getTitle(), author, genres);
        boolean isNewBook = book.getId() == 0;
        book = bookRepository.save(book);
        if (isNewBook) {
            createPermissionForBook(book);
        }
        return book;
    }

    private void createPermissionForBook(Book book) {
        Set<Permission> permissions = Set.of(
                BasePermission.READ,
                BasePermission.WRITE
        );
        aclServiceWrapperService.createPermission(book, permissions);
    }
}
