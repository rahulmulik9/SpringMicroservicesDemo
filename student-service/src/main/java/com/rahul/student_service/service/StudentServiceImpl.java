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
 * Business logic for Student Service.
 *
 * The key addition in Phase 2: this class now talks to the Address
 * Service (via AddressClient) whenever it needs to create or read
 * address data. Student Service never stores address details itself
 * — it only stores addressId, and asks Address Service for the rest.
 */
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final AddressClient addressClient;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO requestDTO) {
        // Step 1: Ask Address Service to create the address first
        Map<String, Object> createdAddress = addressClient.createAddress(requestDTO.getAddress());
        Long addressId = Long.valueOf(createdAddress.get("addressId").toString());

        // Step 2: Save the Student locally, storing only the addressId
        Student student = new Student();
        student.setStudentName(requestDTO.getStudentName());
        student.setAddressId(addressId);

        Student savedStudent = studentRepository.save(student);

        // Step 3: Build the combined response using the address we just created
        AddressResponseDTO addressResponseDTO = mapToAddressResponseDTO(createdAddress);
        return mapToResponseDTO(savedStudent, addressResponseDTO);
    }

    @Override
    public StudentResponseDTO getStudentById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Ask Address Service for the full address details using the stored addressId
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

        // Update the existing address in Address Service (not create a new one)
        String url = "/api/addresses/" + student.getAddressId();
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
        // Note: we are intentionally NOT deleting the address here, to keep
        // Phase 2 simple. This is a known simplification — real systems
        // often handle this with events or cleanup jobs, which is beyond
        // this beginner project's scope.
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