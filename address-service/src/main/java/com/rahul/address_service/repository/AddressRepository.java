package com.rahul.address_service.repository;

import com.rahul.address_service.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * "Repository" layer - talks directly to the database.
 * JpaRepository gives us save(), findById(), findAll(), deleteById(), etc. for free.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
