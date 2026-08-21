package com.rahul.student_service.client;
import com.rahul.student_service.dto.AddressRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This is a Feign Client — notice it's an INTERFACE, not a class.
 * We don't write the HTTP-calling logic ourselves anymore.
 *
 * "name = ADDRESS-SERVICE" tells Feign: don't use a hardcoded URL —
 * ask Eureka where ADDRESS-SERVICE is currently running, and call
 * that instead. This is exactly the name you saw registered on the
 * Eureka dashboard.
 *
 * Spring Cloud generates the actual implementation of this interface
 * automatically at startup — we just declare WHAT calls look like.
 */
@FeignClient(name = "ADDRESS-SERVICE")
public interface AddressClient {

    @PostMapping("/api/addresses")
    Map<String, Object> createAddress(@RequestBody AddressRequestDTO addressRequestDTO);

    @GetMapping("/api/addresses/{addressId}")
    Map<String, Object> getAddressById(@PathVariable("addressId") Long addressId);

    @PutMapping("/api/addresses/{addressId}")
    void updateAddress(@PathVariable("addressId") Long addressId, @RequestBody AddressRequestDTO addressRequestDTO);
}