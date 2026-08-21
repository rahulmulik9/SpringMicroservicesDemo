package com.rahul.student_service.service;

import com.rahul.student_service.dto.StudentRequestDTO;
import com.rahul.student_service.dto.StudentResponseDTO;
import com.rahul.student_service.exception.StudentNotFoundException;
import com.rahul.student_service.model.Student;
import com.rahul.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This is the "Service" layer implementation.
 * This is where the ACTUAL BUSINESS LOGIC lives:
 * - converting DTOs to Models and back
 * - deciding what happens on create/update/delete
 * - throwing exceptions when something is wrong
 *
 * The Controller does NOT contain this logic; it just delegates to this class.
 */
@Service
@RequiredArgsConstructor // Lombok: generates a constructor for all "final" fields (dependency injection)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        Student student = new Student();
        student.setStudentName(requestDTO.getStudentName());
        student.setAddress(requestDTO.getAddress());

        Student savedStudent = studentRepository.save(student);

        return mapToResponseDTO(savedStudent);
    }

    @Override
    public StudentResponseDTO getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        return mapToResponseDTO(student);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public StudentResponseDTO updateStudent(Long studentId, StudentRequestDTO requestDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        student.setStudentName(requestDTO.getStudentName());
        student.setAddress(requestDTO.getAddress());

        Student updatedStudent = studentRepository.save(student);

        return mapToResponseDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.delete(student);
    }

    // Small helper method to convert a Student (Model) into a StudentResponseDTO
    private StudentResponseDTO mapToResponseDTO(Student student) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getStudentName(),
                student.getAddress()
        );
    }
}
