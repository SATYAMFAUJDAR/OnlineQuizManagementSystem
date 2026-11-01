
package com.quiz.onlinequiz.controller;

import com.quiz.onlinequiz.model.Question;
import com.quiz.onlinequiz.model.Result;
import com.quiz.onlinequiz.repository.QuestionRepository;
import com.quiz.onlinequiz.repository.ResultRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AdminController {

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;

    public AdminController(
            QuestionRepository questionRepository,
            ResultRepository resultRepository) {
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
    }

    // ADMIN LOGIN PAGE
    @GetMapping("/admin/login")
    public String loginPage() {
        return "admin-login";
    }

    // ADMIN LOGIN
    @PostMapping("/admin/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        if (username.equals("admin") && password.equals("admin123")) {
            session.setAttribute("adminLoggedIn", true);
            return "redirect:/admin";
        }

        model.addAttribute("error", "Invalid username or password!");
        return "admin-login";
    }

    // ADMIN DASHBOARD
    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        List<Question> questions = questionRepository.findAll();
        List<Result> results = resultRepository.findAll();

        int totalQuestions = questions.size();
        int totalAttempts = results.size();

        double totalScore = 0;
        int totalMarks = 0;
        double highestScore = 0;

        int passed = 0;
        int failed = 0;

        for (Result result : results) {

            totalScore += result.getScore();
            totalMarks += result.getTotal();

            if (result.getTotal() > 0) {

                double percentage =
                        (result.getScore() * 100.0)
                                / result.getTotal();

                if (percentage > highestScore) {
                    highestScore = percentage;
                }

                if (percentage >= 40) {
                    passed++;
                } else {
                    failed++;
                }
            }
        }

        double averageScore = 0;

        if (totalMarks > 0) {
            averageScore = (totalScore * 100.0) / totalMarks;
        }

        model.addAttribute("questions", questions);
        model.addAttribute("results", results);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("averageScore",
                String.format("%.1f", averageScore));
        model.addAttribute("highestScore",
                String.format("%.1f", highestScore));
        model.addAttribute("passed", passed);
        model.addAttribute("failed", failed);

        return "admin";
    }

    // ADMIN LOGOUT
    @GetMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // ADD QUESTION PAGE
    @GetMapping("/admin/question/add")
    public String addQuestionPage(
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        return "add-question";
    }

    // ADD QUESTION
    @PostMapping("/admin/question/add")
    public String addQuestion(
            @RequestParam String category,
            @RequestParam String difficulty,
            @RequestParam String question,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam String correctAnswer,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Question newQuestion = new Question();

        // ID MySQL automatically generate karega
        newQuestion.setCategory(category);
        newQuestion.setDifficulty(difficulty);
        newQuestion.setQuestion(question);
        newQuestion.setOptionA(optionA);
        newQuestion.setOptionB(optionB);
        newQuestion.setOptionC(optionC);
        newQuestion.setOptionD(optionD);
        newQuestion.setCorrectAnswer(correctAnswer);

        questionRepository.save(newQuestion);

        return "redirect:/admin";
    }

    // DELETE QUESTION
    @GetMapping("/admin/question/delete/{id}")
    public String deleteQuestion(
            @PathVariable int id,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        questionRepository.deleteById(id);

        return "redirect:/admin";
    }

    // EDIT QUESTION PAGE
    @GetMapping("/admin/question/edit/{id}")
    public String editQuestionPage(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Question foundQuestion =
                questionRepository.findById(id).orElse(null);

        if (foundQuestion == null) {
            return "redirect:/admin";
        }

        model.addAttribute("question", foundQuestion);

        return "edit-question";
    }

    // UPDATE QUESTION
    @PostMapping("/admin/question/edit")
    public String updateQuestion(
            @RequestParam int id,
            @RequestParam String category,
            @RequestParam String difficulty,
            @RequestParam String question,
            @RequestParam String optionA,
            @RequestParam String optionB,
            @RequestParam String optionC,
            @RequestParam String optionD,
            @RequestParam String correctAnswer,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }

        Question updatedQuestion =
                questionRepository.findById(id).orElse(null);

        if (updatedQuestion == null) {
            return "redirect:/admin";
        }

        updatedQuestion.setCategory(category);
        updatedQuestion.setDifficulty(difficulty);
        updatedQuestion.setQuestion(question);
        updatedQuestion.setOptionA(optionA);
        updatedQuestion.setOptionB(optionB);
        updatedQuestion.setOptionC(optionC);
        updatedQuestion.setOptionD(optionD);
        updatedQuestion.setCorrectAnswer(correctAnswer);

        questionRepository.save(updatedQuestion);

        return "redirect:/admin";
    }

    // ADMIN LOGIN CHECK
    private boolean isAdminLoggedIn(HttpSession session) {

        Boolean loggedIn =
                (Boolean) session.getAttribute("adminLoggedIn");

        return loggedIn != null && loggedIn;
    }
}
