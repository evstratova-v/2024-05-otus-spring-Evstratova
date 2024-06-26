package ru.otus.hw.security;

import ru.otus.hw.domain.Student;

public interface LoginStudentContext {
    void login(Student student);

    Student getStudent();

    boolean isStudentLoggedIn();
}
