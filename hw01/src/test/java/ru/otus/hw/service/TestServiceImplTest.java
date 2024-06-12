package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

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
    void executeTest() {
        Question question = new Question("question1", List.of(
                new Answer("answer1", true),
                new Answer("answer2", false)));

        given(questionDao.findAll()).willReturn(List.of(question));
        given(questionFormatterService.format(question)).willReturn("formattedQuestion");
        testService.executeTest();

        verify(questionFormatterService).format(question);
        verify(ioService).printLine("formattedQuestion");
    }
}
