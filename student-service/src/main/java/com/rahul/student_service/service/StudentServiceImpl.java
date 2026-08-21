package com.rahul.student_service.service;

import com.rahul.student_service.client.AddressClient;
import com.rahul.student_service.dto.AddressResponseDTO;
import com.rahul.student_service.dto.StudentRequestDTO;
import com.rahul.student_service.dto.StudentResponseDTO;
import com.rahul.student_service.exception.StudentNotFoundException;
import com.rahul.student_service.model.Student;
import com.rahul.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AddressClient addressClient;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        Map<String, Object> createdAddress = addressClient.createAddress(requestDTO.getAddress());
        Long addressId = Long.valueOf(createdAddress.get("addressId").toString());

        Student student = new Student();
        student.setStudentName(requestDTO.getStudentName());
        student.setAddressId(addressId);

        Student savedStudent = studentRepository.save(student);

        AddressResponseDTO addressResponseDTO = mapToAddressResponseDTO(createdAddress);
        return mapToResponseDTO(savedStudent, addressResponseDTO);
    }

    @Override
    public StudentResponseDTO getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        Map<String, Object> address = addressClient.getAddressById(student.getAddressId());
        AddressResponseDTO addressResponseDTO = mapToAddressResponseDTO(address);

        return mapToResponseDTO(student, addressResponseDTO);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(student -> {
                    Map<String, Object> address = addressClient.getAddressById(student.getAddressId());
                    AddressResponseDTO addressResponseDTO = mapToAddressResponseDTO(address);
                    return mapToResponseDTO(student, addressResponseDTO);
                })
                .toList();
    }

    @Override
    public StudentResponseDTO updateStudent(Long studentId, StudentRequestDTO requestDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        student.setStudentName(requestDTO.getStudentName());
        addressClient.updateAddress(student.getAddressId(), requestDTO.getAddress());

        Student updatedStudent = studentRepository.save(student);

        Map<String, Object> address = addressClient.getAddressById(student.getAddressId());
        AddressResponseDTO addressResponseDTO = mapToAddressResponseDTO(address);

        return mapToResponseDTO(updatedStudent, addressResponseDTO);
    }

    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        studentRepository.delete(student);
    }

    private StudentResponseDTO mapToResponseDTO(Student student, AddressResponseDTO addressResponseDTO) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getStudentName(),
                addressResponseDTO
        );
    }

    @SuppressWarnings("unchecked")
    private AddressResponseDTO mapToAddressResponseDTO(Map<String, Object> address) {
        return new AddressResponseDTO(
                Long.valueOf(address.get("addressId").toString()),
                (String) address.get("houseName"),
                (String) address.get("streetName"),
                (String) address.get("city"),
                (String) address.get("pincode")
        );
    }
}