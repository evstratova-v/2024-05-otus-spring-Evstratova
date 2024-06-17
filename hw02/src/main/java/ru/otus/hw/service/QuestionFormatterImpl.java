package ru.otus.hw.service;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Question;

@Component
public class QuestionFormatterImpl implements QuestionFormatter {
    @Override
    public String format(Question question) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("%s\n".formatted(question.text()));

        for (int i = 0; i < question.answers().size(); i++) {
            stringBuilder.append("%d. %s\n".formatted(i + 1, question.answers().get(i).text()));
        }
        return stringBuilder.toString();
    }
}
