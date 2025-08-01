package com.example.poc_finops.organization.service;

import com.example.poc_finops.organization.api.dto.CreateUserRequest;
import com.example.poc_finops.organization.api.dto.UserDto;
import com.example.poc_finops.organization.domain.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(UUID id);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(UUID id, CreateUserRequest request);
    void deleteUser(UUID id);
    User getUserByEmail(String email);
    User getUserEntityById(UUID id);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}