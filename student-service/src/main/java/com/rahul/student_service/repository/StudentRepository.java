package com.rahul.student_service.repository;

import com.rahul.student_service.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This is the "Repository" layer.
 * It talks directly to the database.
 *
 * By extending JpaRepository, Spring Data JPA automatically gives us
 * methods like save(), findById(), findAll(), deleteById() etc.
 * We don't have to write any SQL ourselves for basic CRUD operations.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
