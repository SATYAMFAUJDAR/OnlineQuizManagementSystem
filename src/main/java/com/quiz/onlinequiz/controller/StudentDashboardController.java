
package com.quiz.onlinequiz.controller;

import com.quiz.onlinequiz.model.Result;
import com.quiz.onlinequiz.repository.ResultRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentDashboardController {

    private final ResultRepository resultRepository;

    public StudentDashboardController(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        String studentName =
                (String) session.getAttribute("studentName");

        // Student login check
        if (studentName == null) {
            return "redirect:/student";
        }

        // Get results from MySQL
        List<Result> allResults = resultRepository.findAll();
        List<Result> studentResults = new ArrayList<>();

        // Student ke results filter karo
        for (Result result : allResults) {

            if (result.getName() != null
                    && result.getName().equalsIgnoreCase(studentName)) {

                studentResults.add(result);
            }
        }

        int totalQuizzes = studentResults.size();

        double bestScore = 0;
        double totalScore = 0;
        int totalMarks = 0;

        // Statistics calculate karo
        for (Result result : studentResults) {

            if (result.getTotal() > 0) {

                double percentage =
                        (result.getScore() * 100.0)
                                / result.getTotal();

                if (percentage > bestScore) {
                    bestScore = percentage;
                }
            }

            totalScore += result.getScore();
            totalMarks += result.getTotal();
        }

        double averageScore = 0;

        if (totalMarks > 0) {
            averageScore =
                    (totalScore * 100.0) / totalMarks;
        }

        // Data HTML page ko bhejo
        model.addAttribute("studentName", studentName);
        model.addAttribute("results", studentResults);
        model.addAttribute("totalQuizzes", totalQuizzes);

        model.addAttribute(
                "bestScore",
                String.format("%.1f", bestScore)
        );

        model.addAttribute(
                "averageScore",
                String.format("%.1f", averageScore)
        );

        return "student-dashboard";
    }
}
