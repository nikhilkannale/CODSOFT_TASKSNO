package com.codsoft.scrs.dto;

import com.codsoft.scrs.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String courseCode;
    private String title;
    private String description;
    private String instructor;
    private String department;
    private Integer semester;
    private Integer capacity;
    private Integer availableSeats;
    private String schedule;
    private boolean full;

    public static CourseResponse fromEntity(Course c) {
        return CourseResponse.builder()
                .id(c.getId())
                .courseCode(c.getCourseCode())
                .title(c.getTitle())
                .description(c.getDescription())
                .instructor(c.getInstructor())
                .department(c.getDepartment())
                .semester(c.getSemester())
                .capacity(c.getCapacity())
                .availableSeats(c.getAvailableSeats())
                .schedule(c.getSchedule())
                .full(c.isFull())
                .build();
    }
}
