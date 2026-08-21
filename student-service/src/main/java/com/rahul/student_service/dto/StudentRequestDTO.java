package com.rahul.student_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Address details must not be empty")
    @Valid // ensures the nested AddressRequestDTO fields are also validated
    private AddressRequestDTO address;
}