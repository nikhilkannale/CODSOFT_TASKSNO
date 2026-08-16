package com.codsoft.scrs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(columnNames = "course_code"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "course_code", nullable = false, length = 20)
    private String courseCode;

    @NotBlank
    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 100)
    private String instructor;

    @Column(length = 100)
    private String department;

    private Integer semester;

    @Min(1)
    @Column(nullable = false)
    private Integer capacity;

    @Min(0)
    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Column(length = 200)
    private String schedule;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Enrollment> enrollments = new HashSet<>();

    @Transient
    public boolean isFull() {
        return availableSeats == null || availableSeats <= 0;
    }
}
