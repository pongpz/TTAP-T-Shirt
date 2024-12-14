package com.project.ttaptshirt.security;

import com.project.ttaptshirt.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetail implements UserDetails {
    private User user;
    private   Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetail() {
       //constructor no parameter
    }

    public CustomUserDetail(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }
    public User getUser(){
        return this.user;
    }

    public void setUser(User user) {
        this.user = user; // Cập nhật thông tin người dùng
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnable();
    }
}
