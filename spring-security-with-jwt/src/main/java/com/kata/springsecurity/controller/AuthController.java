package com.kata.springsecurity.controller;


import com.kata.springsecurity.config.JWTUtils;
import com.kata.springsecurity.config.TokenBlacklistService;
import com.kata.springsecurity.entity.CustomUser;
import com.kata.springsecurity.modele.UserPresentation;
import com.kata.springsecurity.repository.CustomUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtils jwtUtils;
    private final CustomUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserPresentation user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        CustomUser customUser = CustomUser.builder()
                .username(user.getUsername())
                .password(bCryptPasswordEncoder.encode(user.getPassword()))
                .roles("USER")
                .build();
        CustomUser customUserSaved = userRepository.save(customUser);
        return ResponseEntity.ok(customUserSaved);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserPresentation user) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
            if (authenticate.isAuthenticated()) {
                Map<String, Object> authData = new HashMap<>();
                authData.put("username", user.getUsername());
                authData.put("token", jwtUtils.generateToken(user.getUsername()));
                authData.put("type", "Bearer");
                return ResponseEntity.ok(authData);
            }
            return ResponseEntity.ok(jwtUtils.generateToken(user.getUsername()));
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials", e);
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Date expirationDate = jwtUtils.extractExpiration(token);
            long expiryTimestamp = expirationDate.getTime();

            // On ajoute le token à la blacklist
            tokenBlacklistService.blacklistToken(token, expiryTimestamp);
            return ResponseEntity.ok("Token revoked successfully");
        } else {
            return ResponseEntity.badRequest().body("No Bearer token found in request");
        }
    }


}
