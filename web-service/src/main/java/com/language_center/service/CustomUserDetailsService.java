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

        User user = repository.findByUsername(username);

        if (user == null) {

            throw new UsernameNotFoundException(
                    "Không tìm thấy user");

        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

    }

}