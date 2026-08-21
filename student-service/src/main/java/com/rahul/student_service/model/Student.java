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
 *
 * Note: we no longer store the full address here. We only store
 * addressId — a reference (like a foreign key) to a record that
 * lives in the Address Service's own database. The Student Service
 * does NOT own address data; the Address Service does.
 */
@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    private String studentName;

    // Reference to the Address record living in the Address Service
    private Long addressId;
}