package com.project.ttaptshirt.config;

import com.project.ttaptshirt.util.DateTimeUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {
    @Bean
    public DateTimeUtil dateTimeUtil() {
        return new DateTimeUtil();
    }
}
