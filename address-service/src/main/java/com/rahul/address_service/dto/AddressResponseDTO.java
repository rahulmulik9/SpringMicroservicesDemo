package com.rahul.address_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * "Response DTO" - defines what we send back to the client.
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
