package com.kata.springsecurity.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


    @Configuration
    @Profile("dev")
    public class DevSecurityConfig {


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
        public InMemoryUserDetailsManager userDetailsService() {
            // Définir des utilisateurs en mémoire
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("user")
                    .password("password")
                    .roles("USER")
                    .build();

            UserDetails admin = User.withDefaultPasswordEncoder()
                    .username("admin")
                    .password("adminpass")
                    .roles("ADMIN")
                    .build();

            return new InMemoryUserDetailsManager(user, admin);
        }
    }