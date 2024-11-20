package com.project.ttaptshirt.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ImagesConfig {
    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dy0ss5m6m");
        config.put("api_key", "359917316923629");
        config.put("api_secret", "Ght_aAHLp3wg-L_xaFu5xBnLHcM");
        return new Cloudinary(config);
    }
}
