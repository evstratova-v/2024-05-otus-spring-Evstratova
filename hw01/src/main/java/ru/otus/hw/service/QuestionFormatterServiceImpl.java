package ru.otus.hw.service;

import ru.otus.hw.domain.Question;

public class QuestionFormatterServiceImpl implements QuestionFormatterService {
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
