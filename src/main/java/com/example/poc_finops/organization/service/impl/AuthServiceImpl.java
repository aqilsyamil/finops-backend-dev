package com.example.poc_finops.organization.service.impl;

import com.example.poc_finops.organization.api.dto.JwtResponse;
import com.example.poc_finops.organization.api.dto.LoginRequest;
import com.example.poc_finops.organization.api.dto.UserDto;
import com.example.poc_finops.organization.domain.entity.User;
import com.example.poc_finops.organization.security.JwtUtils;
import com.example.poc_finops.organization.service.AuthService;
import com.example.poc_finops.organization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Override
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);
        
        User user = userService.getUserByEmail(loginRequest.getEmail());
        UserDto userDto = userService.getUserById(user.getId());

        return new JwtResponse(jwt, userDto);
    }
}