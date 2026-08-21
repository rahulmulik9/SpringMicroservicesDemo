package com.rahul.student_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the "Model" (also called Entity).
 * It represents a row in the "student" table in the database.
 * Every field here becomes a column in the table.
 */
@Entity
@Table(name = "student")
@Data               // Lombok: auto-generates getters, setters, toString, equals, hashCode
@NoArgsConstructor   // Lombok: generates an empty constructor
@AllArgsConstructor  // Lombok: generates a constructor with all fields
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generated Student ID
    private Long studentId;

    private String studentName;

    private String address;
}
