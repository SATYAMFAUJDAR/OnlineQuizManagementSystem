
package com.quiz.onlinequiz.repository;

import com.quiz.onlinequiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
