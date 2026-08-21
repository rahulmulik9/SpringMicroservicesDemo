package com.rahul.student_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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