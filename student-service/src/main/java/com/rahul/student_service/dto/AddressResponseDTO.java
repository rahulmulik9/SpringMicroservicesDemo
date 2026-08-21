package com.rahul.student_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the address details we fetched FROM the Address Service,
 * to be embedded inside our own StudentResponseDTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {

    private Long addressId;
    private String houseName;
    private String streetName;
    private String city;
    private String pincode;
}