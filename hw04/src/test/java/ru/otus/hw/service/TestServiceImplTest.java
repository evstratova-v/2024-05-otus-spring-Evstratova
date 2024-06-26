package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = TestServiceImpl.class)
public class TestServiceImplTest {

    @MockBean
    private LocalizedIOService ioService;

    @MockBean
    private QuestionFormatter questionFormatter;

    @MockBean
    private QuestionDao questionDao;

    @Autowired
    private TestService testService;

    @Test
    void testExecuteTestFor() {
        Question question = new Question("question1", List.of(
                new Answer("answer1", true),
                new Answer("answer2", false)));
        Student student = new Student("Masha", "Ivanova");

        given(questionDao.findAll()).willReturn(List.of(question));
        given(questionFormatter.format(question)).willReturn("formattedQuestion");
        given(ioService.readIntForRangeWithPromptLocalized(1, question.answers().size(),
                "TestService.answer.ask",
                "TestService.answer.error")).willReturn(1);
        TestResult testResult = testService.executeTestFor(student);

        verify(questionFormatter).format(question);
        verify(ioService).printLine("formattedQuestion");
        assertThat(testResult).isNotNull()
                .matches(t -> t.getRightAnswersCount() == 1)
                .matches(t -> t.getAnsweredQuestions().equals(List.of(question)));
    }
}
