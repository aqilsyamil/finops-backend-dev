package com.example.poc_finops.organization.service;

import com.example.poc_finops.organization.api.dto.RoleDto;
import com.example.poc_finops.organization.api.dto.UpdateRoleRequest;
import com.example.poc_finops.organization.domain.entity.Role;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleDto> getAllRoles();
    RoleDto getRoleById(UUID roleId);
    RoleDto createRole(UpdateRoleRequest request);
    RoleDto updateRole(UUID roleId, UpdateRoleRequest request);
    void deleteRole(UUID roleId);
    Role getRoleEntityById(UUID roleId);
}