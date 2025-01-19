package com.kata.springsecurity.controller;

import com.kata.springsecurity.config.TestContainersConfig;
import com.kata.springsecurity.config.TestDatabaseConfig;
import com.kata.springsecurity.entity.CustomUser;
import com.kata.springsecurity.repository.CustomUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestContainersConfig.class, TestDatabaseConfig.class})
@ActiveProfiles({"test"})
class DemoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CustomUserRepository customUserRepository;


    @Test
    public void testPublicEndpoint() throws Exception {
        mockMvc.perform(get("/api/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a public endpoint. Anyone can access this."));
    }

    @Test
    public void testProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/protected"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testProtectedEndpointWithUser() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        customUserRepository.save(CustomUser.builder()
                .username("user")
                .password(encodedPassword)
                .roles("USER")
                .build());


        mockMvc.perform(get("/api/protected")
                .with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a protected endpoint. You are authenticated!"));
    }

    @Test
    public void testAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testAdminEndpointWithAdmin() throws Exception {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        customUserRepository.save(CustomUser.builder()
                .username("admin")
                .password(encodedPassword)
                .roles("ADMIN")
                .build());


        mockMvc.perform(get("/api/admin")
                        .with(httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("This is an admin-only endpoint. You are an admin!"));
    }


}