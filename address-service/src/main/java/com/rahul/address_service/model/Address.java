package com.rahul.address_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is the "Model" (Entity) for Address.
 * It represents a row in the "address" table.
 */
@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-generated Address ID
    private Long addressId;

    private String houseName;

    private String streetName;

    private String city;

    private String pincode;
}
