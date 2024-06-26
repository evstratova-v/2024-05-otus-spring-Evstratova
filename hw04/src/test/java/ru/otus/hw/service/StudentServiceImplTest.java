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
        given(ioService.readStringWithPromptLocalized("StudentService.input.first.name")).willReturn("Masha");
        given(ioService.readStringWithPromptLocalized("StudentService.input.last.name")).willReturn("Ivanova");

        Student student = studentService.determineCurrentStudent();
        assertThat(student).isNotNull().matches(s -> s.getFullName().equals("Masha Ivanova"));
    }
}
