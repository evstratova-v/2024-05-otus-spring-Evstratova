package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = StudentServiceImpl.class)
public class StudentServiceImplTest {

    @MockBean
    private LocalizedIOService ioService;

    @Autowired
    private StudentService studentService;

    @Test
    void testDetermineCurrentStudent() {
        String firstName = "Masha";
        String lastName = "Ivanova";
        Student expectedStudent = new Student(firstName, lastName);

        given(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).willReturn(firstName);
        given(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).willReturn(lastName);

        Student student = studentService.determineCurrentStudent();
        assertThat(student).isEqualTo(expectedStudent);
    }
}
