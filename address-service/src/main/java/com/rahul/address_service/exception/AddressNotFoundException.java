package com.rahul.address_service.exception;

/**
 * Thrown when a requested address does not exist in the database.
 */
public class AddressNotFoundException extends RuntimeException {

    public AddressNotFoundException(Long addressId) {
        super("Address not found with id: " + addressId);
    }
}
