
package com.quiz.onlinequiz.controller;

import com.quiz.onlinequiz.model.Result;
import com.quiz.onlinequiz.repository.ResultRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LeaderboardController {

    private final ResultRepository resultRepository;

    public LeaderboardController(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    // LEADERBOARD
    @GetMapping("/leaderboard")
    public String leaderboard(
            @RequestParam(required = false) String category,
            Model model) {

        // Get all quiz results from MySQL
        List<Result> results = resultRepository.findAll();

        // CATEGORY FILTER
        if (category != null
                && !category.trim().isEmpty()
                && !category.equalsIgnoreCase("All")) {

            results = results.stream()
                    .filter(result ->
                            result.getCategory() != null
                                    && result.getCategory()
                                    .equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        // SORT LEADERBOARD
        // First: Highest percentage
        // Second: Highest score

        results.sort(
                Comparator
                        .comparingDouble(this::getPercentage)
                        .reversed()
                        .thenComparing(
                                Comparator.comparingDouble(
                                        Result::getScore
                                ).reversed()
                        )
        );

        // SEND DATA TO HTML
        model.addAttribute("results", results);

        model.addAttribute(
                "selectedCategory",
                category == null ? "All" : category
        );

        return "leaderboard";
    }

    // CALCULATE PERCENTAGE
    private double getPercentage(Result result) {

        if (result == null || result.getTotal() <= 0) {
            return 0;
        }

        return (result.getScore() * 100.0)
                / result.getTotal();
    }
}
