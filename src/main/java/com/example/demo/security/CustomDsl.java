package com.example.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public class CustomDsl extends AbstractHttpConfigurer<CustomDsl, HttpSecurity> {
    @SuppressWarnings("unused")
    private boolean flag;

    @Override
    public void init(HttpSecurity http) throws Exception {
        // any method that adds another configurer
        // must be done in the init method
        http.csrf((csrf) -> csrf.disable());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilter(new JWTAuthenticationFilter(authenticationManager))
                .addFilter(new JWTAuthenticationVerficationFilter(
                        authenticationManager));

    }

    public CustomDsl flag(boolean value) {
        flag = value;
        return this;
    }

    public static CustomDsl customDsl() {
        return new CustomDsl();
    }
}
