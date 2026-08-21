package com.rahul.student_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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