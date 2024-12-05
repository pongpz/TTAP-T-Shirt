package com.project.ttaptshirt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.payos.PayOS;

@SpringBootApplication
@Configuration
public class PayOSConfig implements WebMvcConfigurer {
    @Value("b2dafaec-7ec8-40bc-82e6-a274845d7602")
    private String clientId;

    @Value("d4fd1349-6d6e-4f2b-84a5-6be3f8e78337")
    private String apiKey;

    @Value("d52ec1aa16b5983507a0629f8bbc712b1df7896285754ab1e09d5503ee8cf3ca")
    private String checksumKey;
    

    @Bean
    public PayOS payOS() {
        return new PayOS(clientId, apiKey, checksumKey);
    }
}
