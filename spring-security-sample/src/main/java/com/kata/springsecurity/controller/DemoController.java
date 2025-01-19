package com.kata.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint. Anyone can access this.";
    }

    @GetMapping("/protected")
    public String protectedEndpoint() {
        return "This is a protected endpoint. You are authenticated!";
    }


    @GetMapping("/admin")
    public String adminEndpoint() {
        return "This is an admin-only endpoint. You are an admin!";
    }
}