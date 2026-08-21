package com.rahul.student_service.exception;

/**
 * A custom exception we throw whenever a requested student
 * does not exist in the database (e.g. wrong ID given).
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long studentId) {
        super("Student not found with id: " + studentId);
    }
}
