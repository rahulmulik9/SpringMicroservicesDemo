package com.rahul.student_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This DTO represents the address information the CLIENT sends
 * when creating a student. It mirrors the Address Service's own
 * AddressRequestDTO, but lives here in Student Service because
 * this is what OUR client (Postman) sends to US.
 *
 * We will forward this data to the Address Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {

    @NotBlank(message = "House name must not be empty")
    private String houseName;

    @NotBlank(message = "Street name must not be empty")
    private String streetName;

    @NotBlank(message = "City must not be empty")
    private String city;

    @NotBlank(message = "Pincode must not be empty")
    private String pincode;
}