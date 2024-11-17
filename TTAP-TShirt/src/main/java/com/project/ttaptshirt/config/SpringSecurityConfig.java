package com.project.ttaptshirt.config;

import com.project.ttaptshirt.security.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests((auth) -> auth

                        .requestMatchers("/*").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/TTAP/**").permitAll()
                        .anyRequest().permitAll()
                ).formLogin(login -> login.loginPage("/login").loginProcessingUrl("/login")
                        .usernameParameter("username").passwordParameter("password")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/TTAP/trang-chu"));


        return http.build();
    }

<<<<<<< HEAD
=======
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable().authorizeHttpRequests()
//                .anyRequest().permitAll(); // Cho phép truy cập tất cả các URL mà không cần đăng nhập
//
//        return http.build();
//    }
>>>>>>> 2eb8eeaa5b62252aa3176b66cc22db6d0a953536


    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers("/images/**", "/customcss/**", "/js/**", "/asset/**","customercss/**"));
    }
}
