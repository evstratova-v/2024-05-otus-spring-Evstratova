package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionFormatter questionFormatter;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            printQuestion(question);
            var isAnswerValid = readAnswerForQuestion(question); // Задать вопрос, получить ответ
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private void printQuestion(Question question) {
        String questionFormattedString = questionFormatter.format(question);
        ioService.printLine(questionFormattedString);
    }

    private boolean readAnswerForQuestion(Question question) {
        int numberOfAnswer = ioService.readIntForRangeWithPromptLocalized(1, question.answers().size(),
                "TestService.answer.ask",
                "TestService.answer.error");
        return question.answers().get(numberOfAnswer - 1).isCorrect();
    }

}
