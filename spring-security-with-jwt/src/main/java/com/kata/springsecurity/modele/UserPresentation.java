package com.kata.springsecurity.modele;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserPresentation implements Serializable {
    private String username;
    private String password;
}
