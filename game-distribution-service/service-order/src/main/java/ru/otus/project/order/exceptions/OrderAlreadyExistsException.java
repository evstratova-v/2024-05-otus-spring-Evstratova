package ru.otus.project.order.exceptions;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException() {
        super();
    }

    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
