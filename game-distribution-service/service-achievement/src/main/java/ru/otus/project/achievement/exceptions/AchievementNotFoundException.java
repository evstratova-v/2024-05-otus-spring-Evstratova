package ru.otus.project.achievement.exceptions;

public class AchievementNotFoundException extends RuntimeException {
    public AchievementNotFoundException() {
        super();
    }

    public AchievementNotFoundException(String message) {
        super(message);
    }
}
