package com.rahul.address_service.service;


import com.rahul.address_service.dto.AddressRequestDTO;
import com.rahul.address_service.dto.AddressResponseDTO;
import com.rahul.address_service.exception.AddressNotFoundException;
import com.rahul.address_service.model.Address;
import com.rahul.address_service.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * "Service" implementation - contains the actual business logic:
 * converting DTOs to/from the Model, and coordinating with the Repository.
 */
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public AddressResponseDTO createAddress(AddressRequestDTO requestDTO) {
        Address address = new Address();
        address.setHouseName(requestDTO.getHouseName());
        address.setStreetName(requestDTO.getStreetName());
        address.setCity(requestDTO.getCity());
        address.setPincode(requestDTO.getPincode());

        Address savedAddress = addressRepository.save(address);

        return mapToResponseDTO(savedAddress);
    }

    @Override
    public AddressResponseDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        return mapToResponseDTO(address);
    }

    @Override
    public List<AddressResponseDTO> getAllAddresses() {
        return addressRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public AddressResponseDTO updateAddress(Long addressId, AddressRequestDTO requestDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        address.setHouseName(requestDTO.getHouseName());
        address.setStreetName(requestDTO.getStreetName());
        address.setCity(requestDTO.getCity());
        address.setPincode(requestDTO.getPincode());

        Address updatedAddress = addressRepository.save(address);

        return mapToResponseDTO(updatedAddress);
    }

    @Override
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException(addressId));

        addressRepository.delete(address);
    }

    private AddressResponseDTO mapToResponseDTO(Address address) {
        return new AddressResponseDTO(
                address.getAddressId(),
                address.getHouseName(),
                address.getStreetName(),
                address.getCity(),
                address.getPincode()
        );
    }
}
