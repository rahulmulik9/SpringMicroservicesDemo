package com.rahul.address_service.controller;

import com.rahul.address_service.dto.AddressRequestDTO;
import com.rahul.address_service.dto.AddressResponseDTO;
import com.rahul.address_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * "Controller" layer - entry point for HTTP requests for this service.
 * Receives requests, delegates to the Service layer, returns responses.
 */
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // CREATE - POST http://localhost:8082/api/addresses
    @PostMapping
    public ResponseEntity<AddressResponseDTO> createAddress(
            @Valid @RequestBody AddressRequestDTO requestDTO) {

        AddressResponseDTO response = addressService.createAddress(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ (single) - GET http://localhost:8082/api/addresses/1
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> getAddressById(
            @PathVariable Long addressId) {

        AddressResponseDTO response = addressService.getAddressById(addressId);
        return ResponseEntity.ok(response);
    }

    // READ (all) - GET http://localhost:8082/api/addresses
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> getAllAddresses() {
        List<AddressResponseDTO> response = addressService.getAllAddresses();
        return ResponseEntity.ok(response);
    }

    // UPDATE - PUT http://localhost:8082/api/addresses/1
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDTO requestDTO) {

        AddressResponseDTO response = addressService.updateAddress(addressId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // DELETE - DELETE http://localhost:8082/api/addresses/1
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok("Address with id " + addressId + " deleted successfully.");
    }
}
