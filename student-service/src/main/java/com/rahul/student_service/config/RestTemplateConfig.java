package com.rahul.student_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This class tells Spring how to create a RestTemplate object.
 *
 * RestTemplate is what we'll use to make HTTP calls FROM the Student
 * Service TO the Address Service (a normal, synchronous, blocking call —
 * send a request, wait, get a response, just like Postman does).
 *
 * We define it once here as a @Bean so Spring creates a single shared
 * instance, which we can then @Autowired / inject anywhere we need it
 * (like in our upcoming AddressClient class).
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}