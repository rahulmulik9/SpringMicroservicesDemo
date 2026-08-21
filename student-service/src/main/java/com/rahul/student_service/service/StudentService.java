package com.rahul.student_service.service;


import com.rahul.student_service.dto.StudentRequestDTO;
import com.rahul.student_service.dto.StudentResponseDTO;

import java.util.List;

/**
 * This is the "Service" interface.
 * It defines WHAT operations are available, without saying HOW
 * they are implemented. The Controller talks to this interface,
 * not directly to the implementation class.
 */
public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO requestDTO);

    StudentResponseDTO getStudentById(Long studentId);

    List<StudentResponseDTO> getAllStudents();

    StudentResponseDTO updateStudent(Long studentId, StudentRequestDTO requestDTO);

    void deleteStudent(Long studentId);
}
