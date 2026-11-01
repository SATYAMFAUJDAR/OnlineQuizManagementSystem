
package com.quiz.onlinequiz.controller;

import com.quiz.onlinequiz.model.Question;
import com.quiz.onlinequiz.model.Result;
import com.quiz.onlinequiz.repository.QuestionRepository;
import com.quiz.onlinequiz.repository.ResultRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class QuizController {

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;

    public QuizController(
            QuestionRepository questionRepository,
            ResultRepository resultRepository) {
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
    }

    // START QUIZ
    @GetMapping("/quiz")
    public String startQuiz(
            @RequestParam String category,
            @RequestParam String difficulty,
            HttpSession session,
            Model model) {

        // Get questions from MySQL
        List<Question> allQuestions = questionRepository.findAll();

        // Filter category and difficulty
        List<Question> quizQuestions = new ArrayList<>();

        for (Question q : allQuestions) {

            if (q.getCategory() != null
                    && q.getDifficulty() != null
                    && q.getCategory().equalsIgnoreCase(category)
                    && q.getDifficulty().equalsIgnoreCase(difficulty)) {

                quizQuestions.add(q);
            }
        }

        // No questions available
        if (quizQuestions.isEmpty()) {

            model.addAttribute(
                    "message",
                    "No questions available for this category and difficulty."
            );

            return "quiz";
        }

        // Random order
        Collections.shuffle(quizQuestions);

        // Save questions in session
        session.setAttribute("quizQuestions", quizQuestions);
        session.setAttribute("category", category);

        // Send questions to quiz page
        model.addAttribute("questions", quizQuestions);
        model.addAttribute("category", category);

        return "quiz";
    }

    // SUBMIT QUIZ
    @PostMapping("/quiz/submit")
    public String submitQuiz(
            @RequestParam Map<String, String> answers,
            HttpSession session,
            Model model) {

        // Get quiz questions
        @SuppressWarnings("unchecked")
        List<Question> questions =
                (List<Question>) session.getAttribute("quizQuestions");

        // Student details
        String studentName =
                (String) session.getAttribute("studentName");

        String avatar =
                (String) session.getAttribute("avatar");

        String category =
                (String) session.getAttribute("category");

        // Quiz session missing
        if (questions == null || questions.isEmpty()) {
            return "redirect:/student";
        }

        // MARKING SYSTEM
        double correctMarks = 1.0;
        double negativeMarksPerWrong = 0.25;

        int correct = 0;
        int wrong = 0;
        int skipped = 0;

        double score = 0;

        // CHECK EACH ANSWER
        for (int i = 0; i < questions.size(); i++) {

            Question q = questions.get(i);

            String userAnswer =
                    answers.get("answers[" + i + "]");

            // SKIPPED
            if (userAnswer == null || userAnswer.trim().isEmpty()) {
                skipped++;
                continue;
            }

            String correctAnswer = q.getCorrectAnswer();

            // CORRECT
            if (correctAnswer != null
                    && userAnswer.equalsIgnoreCase(correctAnswer)) {

                correct++;
                score += correctMarks;

            } else {
                // WRONG
                wrong++;
                score -= negativeMarksPerWrong;
            }
        }

        // Score cannot be negative
        if (score < 0) {
            score = 0;
        }

        // ACCURACY / PERCENTAGE
        double accuracy = 0;

        if (!questions.isEmpty()) {
            accuracy = (score * 100.0) / questions.size();
        }

        if (accuracy < 0) {
            accuracy = 0;
        }

        // NEGATIVE MARKS
        double totalNegativeMarks =
                wrong * negativeMarksPerWrong;

        // SAVE RESULT IN MYSQL
        Result result = new Result(
                studentName,
                category,
                score,
                questions.size()
        );

        resultRepository.save(result);

        // SEND DATA TO RESULT.HTML
        model.addAttribute("name", studentName);
        model.addAttribute("avatar", avatar);
        model.addAttribute("category", category);
        model.addAttribute("score", score);
        model.addAttribute("total", questions.size());
        model.addAttribute("correct", correct);
        model.addAttribute("wrong", wrong);
        model.addAttribute("skipped", skipped);
        model.addAttribute("negativeMarks", totalNegativeMarks);

        // Formatted accuracy
        model.addAttribute(
                "accuracy",
                String.format("%.1f", accuracy)
        );

        // Numeric accuracy
        model.addAttribute("accuracyValue", accuracy);

        // SAVE LAST RESULT FOR CERTIFICATE
        session.setAttribute("lastScore", score);
        session.setAttribute("lastTotal", questions.size());

        session.setAttribute(
                "lastAccuracy",
                String.format("%.1f", accuracy)
        );

        // Remove quiz questions
        session.removeAttribute("quizQuestions");

        return "result";
    }
}
