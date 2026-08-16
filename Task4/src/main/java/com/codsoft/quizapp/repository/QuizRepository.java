package com.codsoft.quizapp.repository;

import com.codsoft.quizapp.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Fetch-joined variants so the transient getTotalMarks()/getQuestionCount() getters
    // (used by the JSON response) can safely read `questions` outside a transaction
    // even with spring.jpa.open-in-view=false.

    @Query("select distinct q from Quiz q left join fetch q.questions where q.active = true")
    List<Quiz> findByActiveTrue();

    @Query("select distinct q from Quiz q left join fetch q.questions")
    List<Quiz> findAllWithQuestions();

    @Query("select distinct q from Quiz q left join fetch q.questions where q.id = :id")
    Optional<Quiz> findByIdWithQuestions(Long id);
}
