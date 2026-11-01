package com.quiz.onlinequiz.repository;

import com.quiz.onlinequiz.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {
}