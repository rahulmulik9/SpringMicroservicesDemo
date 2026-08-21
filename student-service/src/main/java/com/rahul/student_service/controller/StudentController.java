package com.rahul.student_service.controller;

import com.rahul.student_service.dto.StudentRequestDTO;
import com.rahul.student_service.dto.StudentResponseDTO;
import com.rahul.student_service.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This is the "Controller" layer.
 * It is the ENTRY POINT for all HTTP requests coming into this service.
 *
 * The Controller's job is small and simple:
 *  1. Receive the HTTP request.
 *  2. Pass the data to the Service layer.
 *  3. Return the Service's result back as an HTTP response.
 *
 * It does NOT contain business logic itself.
 */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    // CREATE - POST http://localhost:8081/api/students
    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO response = studentService.createStudent(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ (single) - GET http://localhost:8081/api/students/1
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudentById(
            @PathVariable Long studentId) {

        StudentResponseDTO response = studentService.getStudentById(studentId);
        return ResponseEntity.ok(response);
    }

    // READ (all) - GET http://localhost:8081/api/students
    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> response = studentService.getAllStudents();
        return ResponseEntity.ok(response);
    }

    // UPDATE - PUT http://localhost:8081/api/students/1
    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody StudentRequestDTO requestDTO) {

        StudentResponseDTO response = studentService.updateStudent(studentId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // DELETE - DELETE http://localhost:8081/api/students/1
    @DeleteMapping("/{studentId}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok("Student with id " + studentId + " deleted successfully.");
    }
}
