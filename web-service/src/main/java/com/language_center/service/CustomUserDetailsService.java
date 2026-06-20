package com.language_center.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.language_center.entity.User;
import com.language_center.repository.UserRepository;

@Service
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(
            UserRepository repository) {

        this.repository = repository;

    }

    @Override
    public UserDetails loadUserByUsername(
            String username)
            throws UsernameNotFoundException {

        String normalizedUsername = username == null ? "" : username.trim();

        User user = repository.findByUsernameIgnoreCase(normalizedUsername);

        if (user == null) {
            user = repository.findByUsername(normalizedUsername);
        }

        if (user == null) {

            throw new UsernameNotFoundException(
                    "Không tìm thấy user");

        }

        String role = normalizeRole(user.getRole());

        String password = user.getPassword() == null ? "" : user.getPassword();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(password)
                .roles(role)
                .build();

    }

    private String normalizeRole(String role) {

        if (role == null) {
            return "ADMIN";
        }

        String normalizedRole = role.trim().toUpperCase();

        if (normalizedRole.startsWith("ROLE_")) {
            normalizedRole = normalizedRole.substring(5);
        }

        return normalizedRole;

    }

}