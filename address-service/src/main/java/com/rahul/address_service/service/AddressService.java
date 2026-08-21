package com.rahul.address_service.service;


import com.rahul.address_service.dto.AddressRequestDTO;
import com.rahul.address_service.dto.AddressResponseDTO;

import java.util.List;

/**
 * "Service" interface - defines WHAT operations are available.
 */
public interface AddressService {

    AddressResponseDTO createAddress(AddressRequestDTO requestDTO);

    AddressResponseDTO getAddressById(Long addressId);

    List<AddressResponseDTO> getAllAddresses();

    AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO requestDTO);

    void deleteAddress(Long addressId);
}
