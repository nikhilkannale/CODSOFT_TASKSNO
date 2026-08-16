package com.codsoft.quizapp.repository;

import com.codsoft.quizapp.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("select r from Result r join fetch r.user join fetch r.quiz where r.user.id = :userId order by r.submittedAt desc")
    List<Result> findByUserIdOrderBySubmittedAtDesc(Long userId);

    @Query("select r from Result r join fetch r.user join fetch r.quiz where r.quiz.id = :quizId order by r.score desc, r.timeTakenInSeconds asc")
    List<Result> findByQuizIdOrderByScoreDescTimeTakenInSecondsAsc(Long quizId);

    @Query("select r from Result r join fetch r.user join fetch r.quiz order by r.submittedAt desc")
    List<Result> findAllByOrderBySubmittedAtDesc();
}
