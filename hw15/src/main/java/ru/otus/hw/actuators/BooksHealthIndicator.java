package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.BookService;

@RequiredArgsConstructor
@Component
public class BooksHealthIndicator implements HealthIndicator {

    private final BookService bookService;

    @Override
    public Health health() {
        if (bookService.findFirst().isEmpty()) {
            return Health.down()
                    .status(Status.DOWN)
                    .withDetail("message", "В библиотеке нет книг!")
                    .build();
        } else {
            return Health.up().withDetail("message", "В библиотеке есть книги").build();
        }
    }
}
