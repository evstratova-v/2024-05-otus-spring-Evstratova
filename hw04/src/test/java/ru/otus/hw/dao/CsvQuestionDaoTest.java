package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = CsvQuestionDao.class)
public class CsvQuestionDaoTest {

    @MockBean
    private TestFileNameProvider testFileNameProvider;

    @Autowired
    private QuestionDao questionDao;

    @Test
    void testFindAll() {
        List<Question> expectedQuestions = List.of(
                new Question("Is this test question?", List.of(
                        new Answer("Yes", true),
                        new Answer("No", false),
                        new Answer("Nobody knows", false))),
                new Question("Is this first test question?", List.of(
                        new Answer("Yes", false),
                        new Answer("No", true),
                        new Answer("Questions have no order", false))));
        given(testFileNameProvider.getTestFileName()).willReturn("testQuestions.csv");

        List<Question> questions = questionDao.findAll();
        assertThat(questions).isNotEmpty().isEqualTo(expectedQuestions);
    }
}
