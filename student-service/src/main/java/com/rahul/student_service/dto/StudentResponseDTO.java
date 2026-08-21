package com.rahul.student_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the "Response DTO".
 * It defines exactly what data we SEND BACK to the client.
 *
 * Here it looks similar to the Request DTO, but in real projects
 * the response often contains extra computed fields (like studentId)
 * while hiding sensitive internal fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long studentId;
    private String studentName;
    private String address;
}
