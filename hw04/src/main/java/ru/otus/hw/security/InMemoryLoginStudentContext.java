package ru.otus.hw.security;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Student;

import static java.util.Objects.nonNull;

@Getter
@Component
public class InMemoryLoginStudentContext implements LoginStudentContext {
    private Student student;

    @Override
    public void login(Student student) {
        this.student = student;
    }

    @Override
    public boolean isStudentLoggedIn() {
        return nonNull(student);
    }
}
