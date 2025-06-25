package com.bank.gugu.global.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long id;

    public CustomUserDetails(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             Long id) {
        super(username, password, authorities);
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "CustomUserDetails(id=%d, username=%s, authorities=%s)",
                id, getUsername(), getAuthorities()
        );
    }
}
