package ru.otus.project.achievement.exceptions;

public class AchievementEventAlreadyExistException extends RuntimeException {
    public AchievementEventAlreadyExistException() {
        super();
    }

    public AchievementEventAlreadyExistException(String message) {
        super(message);
    }
}
