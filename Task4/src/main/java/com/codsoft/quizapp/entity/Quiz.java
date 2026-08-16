package com.codsoft.quizapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /** Duration of the quiz in seconds. */
    @Min(10)
    @Column(nullable = false)
    private Integer durationInSeconds;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Excluded from equals/hashCode/toString: it's the other side of a bidirectional
    // relationship with Question.quiz, and Lombok's generated methods would otherwise
    // recurse into each other infinitely (Quiz -> Question -> Quiz -> ...).
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private List<Question> questions = new ArrayList<>();

    /**
     * These are read by Jackson whenever a Quiz is serialized -- including when a Quiz is
     * nested inside a Result (e.g. admin results/leaderboard), where `questions` was never
     * fetch-joined. Guard with Hibernate.isInitialized so an uninitialized lazy proxy
     * degrades to 0 instead of throwing LazyInitializationException.
     */
    @Transient
    public int getTotalMarks() {
        if (questions == null || !org.hibernate.Hibernate.isInitialized(questions)) return 0;
        return questions.stream().mapToInt(Question::getMarks).sum();
    }

    @Transient
    public int getQuestionCount() {
        if (questions == null || !org.hibernate.Hibernate.isInitialized(questions)) return 0;
        return questions.size();
    }
}
