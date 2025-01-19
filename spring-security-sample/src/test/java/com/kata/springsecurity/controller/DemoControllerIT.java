package com.kata.springsecurity.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
class DemoControllerIT {

    @Autowired
    private MockMvc mockMvc;




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
        mockMvc.perform(get("/api/admin")
                .with(httpBasic("admin", "adminpass")))
                .andExpect(status().isOk())
                .andExpect(content().string("This is an admin-only endpoint. You are an admin!"));
    }

}