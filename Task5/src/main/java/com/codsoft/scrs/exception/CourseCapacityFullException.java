package com.codsoft.scrs.exception;

public class CourseCapacityFullException extends RuntimeException {
    public CourseCapacityFullException(String message) {
        super(message);
    }
}
