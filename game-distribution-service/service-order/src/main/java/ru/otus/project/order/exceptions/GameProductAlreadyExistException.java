package ru.otus.project.order.exceptions;

public class GameProductAlreadyExistException extends RuntimeException {
    public GameProductAlreadyExistException() {
        super();
    }

    public GameProductAlreadyExistException(String message) {
        super(message);
    }
}
