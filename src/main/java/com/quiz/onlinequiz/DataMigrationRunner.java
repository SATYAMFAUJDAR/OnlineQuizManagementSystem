
package com.quiz.onlinequiz;

import com.quiz.onlinequiz.model.Question;
import com.quiz.onlinequiz.model.Result;
import com.quiz.onlinequiz.repository.QuestionRepository;
import com.quiz.onlinequiz.repository.ResultRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;

    public DataMigrationRunner(
            QuestionRepository questionRepository,
            ResultRepository resultRepository) {

        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Import Questions
        Path questionFile = Path.of("questions.txt");

        if (Files.exists(questionFile)
                && questionRepository.count() == 0) {

            try (BufferedReader br =
                         Files.newBufferedReader(questionFile)) {

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] data = line.split("\\|", -1);

                    if (data.length >= 9) {

                        Question q = new Question();

                        q.setCategory(data[1]);
                        q.setDifficulty(data[2]);
                        q.setQuestion(data[3]);
                        q.setOptionA(data[4]);
                        q.setOptionB(data[5]);
                        q.setOptionC(data[6]);
                        q.setOptionD(data[7]);
                        q.setCorrectAnswer(data[8]);

                        questionRepository.save(q);
                    }
                }
            }

            System.out.println("Questions imported successfully!");
        }

        // Import Results
        Path resultFile = Path.of("results.txt");

        if (Files.exists(resultFile)
                && resultRepository.count() == 0) {

            try (BufferedReader br =
                         Files.newBufferedReader(resultFile)) {

                String line;

                while ((line = br.readLine()) != null) {

                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] data = line.split("\\|", -1);

                    if (data.length >= 4) {

                        Result result = new Result();

                        result.setName(data[0]);
                        result.setCategory(data[1]);
                        result.setScore(
                                Double.parseDouble(data[2])
                        );
                        result.setTotal(
                                Integer.parseInt(data[3])
                        );

                        resultRepository.save(result);
                    }
                }
            }

            System.out.println("Results imported successfully!");
        }

        System.out.println("Data migration check completed!");
    }
}
