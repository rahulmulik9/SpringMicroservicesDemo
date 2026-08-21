package com.rahul.student_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * This is the "ErrorResponse DTO".
 * Whenever something goes wrong (e.g. student not found, invalid input),
 * we send back a consistent, predictable error shape instead of a raw
 * stack trace. This makes it much easier for API consumers (like a
 * frontend app or Postman) to handle errors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
