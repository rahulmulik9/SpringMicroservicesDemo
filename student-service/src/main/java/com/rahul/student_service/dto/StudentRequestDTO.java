package com.rahul.student_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the "Request DTO" (Data Transfer Object).
 * It defines exactly what data the CLIENT must send to us
 * when creating or updating a student.
 *
 * We never expose the Model (Student.java) directly to the outside world.
 * This keeps our internal database structure separate from the API contract.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "Student name must not be empty")
    private String studentName;

    @NotBlank(message = "Address must not be empty")
    private String address;
}
