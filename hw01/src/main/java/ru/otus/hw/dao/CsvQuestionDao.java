package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        try (InputStream inputStream = new ClassPathResource(fileNameProvider.getTestFileName()).getInputStream()) {
            return getQuestionsFromCsv(inputStream);
        } catch (IOException e) {
            throw new QuestionReadException("Error while reading questions from file %s".formatted(
                    fileNameProvider.getTestFileName()), e);
        }
    }

    private List<Question> getQuestionsFromCsv(InputStream inputStream) {
        CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(new InputStreamReader(inputStream))
                .withType(QuestionDto.class)
                .withSeparator(';')
                .withSkipLines(1)
                .build();
        return csvToBean.parse().stream().map(QuestionDto::toDomainObject).toList();
    }
}
