package com.example.poc_finops.organization.service;

import com.example.poc_finops.organization.api.dto.JwtResponse;
import com.example.poc_finops.organization.api.dto.LoginRequest;

public interface AuthService {
    JwtResponse authenticate(LoginRequest loginRequest);
}