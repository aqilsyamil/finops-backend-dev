package com.example.poc_finops.organization.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;
    
    public JwtResponse(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
}