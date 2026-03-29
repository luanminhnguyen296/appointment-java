package com.example.appointment.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {
    private final String imageUrl;
    private final String fullName;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String imageUrl, String fullName) {
        super(username, password, authorities);
        this.imageUrl = imageUrl;
        this.fullName = fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFullName() {
        return fullName;
    }
}
