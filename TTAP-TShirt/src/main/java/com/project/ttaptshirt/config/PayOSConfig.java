package com.project.ttaptshirt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.payos.PayOS;

@SpringBootApplication
@Configuration
public class PayOSConfig implements WebMvcConfigurer {
    @Value("9d9028ba-a8f6-4cae-a730-042d130962b4")
    private String clientId;

    @Value("61c00f9f-aedb-4e1e-9a5a-13c9d5964491")
    private String apiKey;

    @Value("7b6fa747a23a63c2ed851c1bd46a3b7b1a57087a7b5d16050fe8d29667cb8183")
    private String checksumKey;

//    @Value("b2dafaec-7ec8-40bc-82e6-a274845d7602")
//    private String clientId;
//
//    @Value("d4fd1349-6d6e-4f2b-84a5-6be3f8e78337")
//    private String apiKey;
//
//    @Value("d52ec1aa16b5983507a0629f8bbc712b1df7896285754ab1e09d5503ee8cf3ca")
//    private String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
