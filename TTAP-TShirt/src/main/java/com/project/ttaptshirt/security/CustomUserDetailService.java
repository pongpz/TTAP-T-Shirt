package com.project.ttaptshirt.security;


import com.project.ttaptshirt.entity.Role;
import com.project.ttaptshirt.entity.TaiKhoan;
import com.project.ttaptshirt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;


@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username
        TaiKhoan user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Retrieve the role of the user
        Role role = user.getRole();
        if (role == null) {
            throw new UsernameNotFoundException("User role not found");
        }

        // Create GrantedAuthority from the role
        Collection<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        grantedAuthoritySet.add(new SimpleGrantedAuthority(role.getRoleName()));

        // Return CustomUserDetail object
        return new CustomUserDetail(user, grantedAuthoritySet);
    }
}
