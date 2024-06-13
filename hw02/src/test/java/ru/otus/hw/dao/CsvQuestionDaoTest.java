package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    @Mock
    private TestFileNameProvider testFileNameProvider;

    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

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

        List<Question> questions = csvQuestionDao.findAll();
        assertThat(questions).isNotEmpty().isEqualTo(expectedQuestions);
    }
}
