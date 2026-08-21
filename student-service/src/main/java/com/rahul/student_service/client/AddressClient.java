package com.rahul.student_service.client;

import com.rahul.student_service.dto.AddressRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This class is responsible for ALL communication between
 * Student Service and Address Service.
 *
 * Keeping this logic in its own class (instead of scattering
 * RestTemplate calls inside StudentServiceImpl) makes it very
 * clear, at a glance, exactly how and where the two services talk.
 */
@Component
@RequiredArgsConstructor
public class AddressClient {

    private final RestTemplate restTemplate;

    // Read from application.properties: address-service.url=http://localhost:8082
    @Value("${address-service.url}")
    private String addressServiceUrl;

    /**
     * Calls: POST {addressServiceUrl}/api/addresses
     * Sends the address details, and gets back the created address
     * (including its generated addressId).
     */
    public Map<String, Object> createAddress(AddressRequestDTO addressRequestDTO) {
        String url = addressServiceUrl + "/api/addresses";
        return restTemplate.postForObject(url, addressRequestDTO, Map.class);
    }

    /**
     * Calls: GET {addressServiceUrl}/api/addresses/{addressId}
     * Fetches the full address details for a given addressId.
     */
    public Map<String, Object> getAddressById(Long addressId) {
        String url = addressServiceUrl + "/api/addresses/" + addressId;
        return restTemplate.getForObject(url, Map.class);
    }


    public void updateAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        String url = addressServiceUrl + "/api/addresses/" + addressId;
        restTemplate.put(url, addressRequestDTO);
    }
}