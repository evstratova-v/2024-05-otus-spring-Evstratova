package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

public class TestServiceImplTest {

    @Mock
    StreamsIOService ioService;

    @Mock
    CsvQuestionDao questionDao;

    TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    void executeTest() {
        given(questionDao.findAll()).willReturn(List.of(new Question("question1", List.of(
                new Answer("answer1", true),
                new Answer("answer2", false)))));
        testService.executeTest();

        verify(ioService).printLine("question1");
        verify(ioService).printFormattedLine("%d. %s", 1, "answer1");
        verify(ioService).printFormattedLine("%d. %s", 2, "answer2");
    }
}
