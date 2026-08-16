package com.codsoft.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Excluded from equals/hashCode/toString to avoid infinite recursion with Quiz.questions
    // (see the matching exclusion on that field).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Quiz quiz;

    @NotBlank
    @Column(nullable = false, length = 1000)
    private String questionText;

    @NotBlank
    @Column(nullable = false)
    private String optionA;

    @NotBlank
    @Column(nullable = false)
    private String optionB;

    @NotBlank
    @Column(nullable = false)
    private String optionC;

    @NotBlank
    @Column(nullable = false)
    private String optionD;

    /** One of "A", "B", "C", "D". */
    @NotBlank
    @Column(nullable = false, length = 1)
    private String correctOption;

    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private Integer marks = 1;
}
