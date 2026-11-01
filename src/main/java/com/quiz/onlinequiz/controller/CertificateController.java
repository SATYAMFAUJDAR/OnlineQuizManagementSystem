package com.quiz.onlinequiz.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class CertificateController {

    @GetMapping("/certificate")
    public String certificate(
            HttpSession session,
            Model model) {

        String studentName =
                (String) session.getAttribute("studentName");

        String avatar =
                (String) session.getAttribute("avatar");

        String category =
                (String) session.getAttribute("category");

        Object score =
                session.getAttribute("lastScore");

        Object total =
                session.getAttribute("lastTotal");

        Object accuracy =
                session.getAttribute("lastAccuracy");


        // Student not available
        if (studentName == null) {
            return "redirect:/student";
        }


        model.addAttribute(
                "name",
                studentName
        );

        model.addAttribute(
                "avatar",
                avatar
        );

        model.addAttribute(
                "category",
                category
        );

        model.addAttribute(
                "score",
                score
        );

        model.addAttribute(
                "total",
                total
        );

        model.addAttribute(
                "accuracy",
                accuracy
        );


        // Current date
        String date =
                LocalDate.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd MMMM yyyy"
                                )
                        );

        model.addAttribute(
                "date",
                date
        );


        return "certificate";
    }
}