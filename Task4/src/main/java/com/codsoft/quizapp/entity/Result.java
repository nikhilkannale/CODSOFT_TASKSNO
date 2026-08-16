package com.codsoft.quizapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Excluded from Lombok's generated toString to avoid triggering lazy-loading
    // (and a large nested dump) whenever a Result is logged or printed.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @lombok.ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @lombok.ToString.Exclude
    private Quiz quiz;

    @Column(nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Integer attemptedQuestions;

    @Column(nullable = false)
    private Integer correctAnswers;

    @Column(nullable = false)
    private Integer incorrectAnswers;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalMarks;

    @Column(nullable = false)
    private Double percentage;

    @Column(nullable = false)
    private Boolean passed;

    /** Time taken by the student in seconds. */
    @Column(nullable = false)
    private Integer timeTakenInSeconds;

    @Builder.Default
    private LocalDateTime submittedAt = LocalDateTime.now();
}
