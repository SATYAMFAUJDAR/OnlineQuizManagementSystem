package com.quiz.onlinequiz;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/student")
    public String student() {
        return "student";
    }
    @PostMapping("/student")
    public String studentDetails(
            @RequestParam String name,
            @RequestParam String avatar,
            HttpSession session,
            Model model) {

        if (name.trim().isEmpty()) {
            return "redirect:/student";
        }

        model.addAttribute("name", name);
        model.addAttribute("avatar", avatar);

        session.setAttribute("studentName", name);
        session.setAttribute("avatar", avatar);

        return "category";
    }
}