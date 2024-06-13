package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionFormatterService questionFormatterService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void testExecuteTestFor() {
        Question question = new Question("question1", List.of(
                new Answer("answer1", true),
                new Answer("answer2", false)));
        Student student = new Student("Masha", "Ivanova");

        given(questionDao.findAll()).willReturn(List.of(question));
        given(questionFormatterService.format(question)).willReturn("formattedQuestion");
        given(ioService.readIntForRangeWithPrompt(1, question.answers().size(),
                "Please type the number of correct answer: ",
                "Your answer does not match any of the possible answers")).willReturn(1);
        TestResult testResult = testService.executeTestFor(student);

        verify(questionFormatterService).format(question);
        verify(ioService).printLine("formattedQuestion");
        assertThat(testResult).isNotNull()
                .matches(t -> t.getRightAnswersCount() == 1)
                .matches(t -> t.getAnsweredQuestions().equals(List.of(question)));
    }
}
