package com.kata.springsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.springsecurity.config.TestContainersConfig;
import com.kata.springsecurity.config.TestDatabaseConfig;
import com.kata.springsecurity.modele.UserPresentation;
import com.kata.springsecurity.repository.CustomUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestContainersConfig.class, TestDatabaseConfig.class})
@ActiveProfiles({"test"})
class DemoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CustomUserRepository customUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Nettoyer la base si nécessaire
        customUserRepository.deleteAll();
    }

    @Test
    void testRegisterLoginAndAccessProtectedEndpoint() throws Exception {


        // creer un utilisateur et le sauvegarder
        UserPresentation newUser = new UserPresentation();
        newUser.setUsername("bob");
        newUser.setPassword("secret");
        newUser.setAdmin(false);

        String userJson = objectMapper.writeValueAsString(newUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());

        // verifier que l'utilisateur est bien enregistré et recuperer le token
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("bob"))
                .andReturn()
                .getResponse()
                .getContentAsString();


        Map<?, ?> responseMap = objectMapper.readValue(loginResponse, Map.class);
        String token = (String) responseMap.get("token");

        // test d'une route protegée ou l'utilisateur doit etre autorisé
        mockMvc.perform(get("/api/protected")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("This is a protected endpoint. You are authenticated!"));


        // test d'une route admin ou l'utilisateur n'est pas autorisé
        mockMvc.perform(get("/api/admin")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());



        // test de la deconnexion
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Token revoked successfully"));


        // test d'une route protegée ou l'utilisateur n'est plus autorisé
        mockMvc.perform(get("/api/protected")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

    }


}