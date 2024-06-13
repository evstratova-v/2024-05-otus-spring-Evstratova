package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.domain.Student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplTest {

    @Mock
    private IOService ioService;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void testDetermineCurrentStudent() {
        given(ioService.readStringWithPrompt("Please input your first name")).willReturn("Masha");
        given(ioService.readStringWithPrompt("Please input your last name")).willReturn("Ivanova");

        Student student = studentService.determineCurrentStudent();
        assertThat(student).isNotNull().matches(s -> s.getFullName().equals("Masha Ivanova"));
    }
}
