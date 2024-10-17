package com.project.ttaptshirt.security;

//import com.tunesmusic.model.Role;
//import com.tunesmusic.model.User;
//import com.tunesmusic.service.UserService;

import com.project.ttaptshirt.entity.Role;
import com.project.ttaptshirt.entity.User;
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
import java.util.List;


@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("user not found");
        }

        Collection<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
//        Set<UserRole> roles = user.getUserRoles();
//        for (UserRole userRole : roles
//             ) {
//            grantedAuthoritySet.add(new SimpleGrantedAuthority(userRole.getRole().getRoleName()));
//        }
        List<Role> roles = user.getRoles();
        for (Role role : roles
        ) {
            grantedAuthoritySet.add(new SimpleGrantedAuthority(role.getRoleName()));
        }


        return new CustomUserDetail(user, grantedAuthoritySet);
    }
}
