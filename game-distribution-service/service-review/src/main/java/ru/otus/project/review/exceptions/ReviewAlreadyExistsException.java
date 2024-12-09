package ru.otus.project.review.exceptions;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super();
    }

    public ReviewAlreadyExistsException(String message) {
        super(message);
    }

}
