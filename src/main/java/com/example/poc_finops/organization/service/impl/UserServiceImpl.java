package com.example.poc_finops.organization.service.impl;

import com.example.poc_finops.organization.api.dto.CreateUserRequest;
import com.example.poc_finops.organization.api.dto.OrganizationDto;
import com.example.poc_finops.organization.api.dto.RoleDto;
import com.example.poc_finops.organization.api.dto.UserDto;
import com.example.poc_finops.organization.domain.entity.Organization;
import com.example.poc_finops.organization.domain.entity.Role;
import com.example.poc_finops.organization.domain.entity.User;
import com.example.poc_finops.organization.domain.valueobject.UserStatus;
import com.example.poc_finops.organization.repository.UserRepository;
import com.example.poc_finops.organization.service.OrganizationService;
import com.example.poc_finops.organization.service.RoleService;
import com.example.poc_finops.organization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationService organizationService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        User user = getUserEntityById(id);
        return convertToDto(user);
    }

    @Override
    public UserDto createUser(CreateUserRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        Organization organization = organizationService.getOrganizationEntityById(request.getOrganizationId());
        Role role = roleService.getRoleEntityById(request.getRoleId());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setOrganization(organization);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE.getValue());

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UUID id, CreateUserRequest request) {
        User user = getUserEntityById(id);
        
        if (!user.getEmail().equals(request.getEmail()) && existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        if (!user.getUsername().equals(request.getUsername()) && existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        Organization organization = organizationService.getOrganizationEntityById(request.getOrganizationId());
        Role role = roleService.getRoleEntityById(request.getRoleId());

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setOrganization(organization);
        user.setRole(role);
        
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return convertToDto(updatedUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setLastAccessAt(user.getLastAccessAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        if (user.getOrganization() != null) {
            OrganizationDto orgDto = new OrganizationDto();
            orgDto.setId(user.getOrganization().getId());
            orgDto.setOrganizationName(user.getOrganization().getOrganizationName());
            dto.setOrganization(orgDto);
        }
        
        if (user.getRole() != null) {
            RoleDto roleDto = new RoleDto();
            roleDto.setId(user.getRole().getId());
            roleDto.setRoleName(user.getRole().getRoleName());
            roleDto.setRoleType(user.getRole().getRoleType());
            roleDto.setStatus(user.getRole().getStatus());
            dto.setRole(roleDto);
        }
        
        return dto;
    }
}