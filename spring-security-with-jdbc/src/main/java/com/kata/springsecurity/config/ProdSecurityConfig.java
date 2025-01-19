package com.kata.springsecurity.config;


import com.kata.springsecurity.repository.CustomUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;


@Configuration
@Profile("prod")
@AllArgsConstructor
public class ProdSecurityConfig {


    private final MyUserDetailService customUserDetailService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuration des autorisations
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public").permitAll() // Accessible à tous
                        .requestMatchers("/api/admin").hasRole("ADMIN") // Accessible uniquement aux utilisateurs avec le rôle ADMIN
                        .anyRequest().authenticated()              // Nécessite une authentification pour les autres endpoints
                )
                // Authentification HTTP Basic
                .httpBasic(Customizer.withDefaults())
                // Désactiver CSRF pour simplifier les tests REST
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailService;
    }

    // AuthenticationProvider qui s’appuie sur le UserDetailsService et BCrypt
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        return provider;
    }

    // Encoder de mots de passe
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}