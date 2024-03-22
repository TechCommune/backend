package com.thbs.backend.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class LoginModel {
    private String email;
    private String password;
    
}
