package com.example.poc_finops.organization.service.impl;

import com.example.poc_finops.organization.api.dto.RoleDto;
import com.example.poc_finops.organization.api.dto.UpdateRoleRequest;
import com.example.poc_finops.organization.domain.entity.Role;
import com.example.poc_finops.organization.repository.RoleRepository;
import com.example.poc_finops.organization.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDto getRoleById(UUID roleId) {
        Role role = getRoleEntityById(roleId);
        return convertToDto(role);
    }

    @Override
    public RoleDto createRole(UpdateRoleRequest request) {
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setRoleType(request.getRoleType());
        role.setStatus(request.getStatus());

        Role savedRole = roleRepository.save(role);
        return convertToDto(savedRole);
    }

    @Override
    public RoleDto updateRole(UUID roleId, UpdateRoleRequest request) {
        Role role = getRoleEntityById(roleId);
        role.setRoleName(request.getRoleName());
        role.setRoleType(request.getRoleType());
        role.setStatus(request.getStatus());

        Role updatedRole = roleRepository.save(role);
        return convertToDto(updatedRole);
    }

    @Override
    public void deleteRole(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role not found with id: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleEntityById(UUID roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
    }

    private RoleDto convertToDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setRoleName(role.getRoleName());
        dto.setRoleType(role.getRoleType());
        dto.setStatus(role.getStatus());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        dto.setCreatedBy(role.getCreatedBy());
        dto.setUpdatedBy(role.getUpdatedBy());
        return dto;
    }
}