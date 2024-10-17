package com.project.ttaptshirt.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class testPassword {
    public static void main(String[] args) {
        System.out.println( new BCryptPasswordEncoder().encode("123"));
    }
}
