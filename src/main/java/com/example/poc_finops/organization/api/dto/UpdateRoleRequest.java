package com.example.poc_finops.organization.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleRequest {
    @NotBlank(message = "Role name is required")
    private String roleName;
    
    @NotBlank(message = "Role type is required")
    private String roleType;
    
    @NotBlank(message = "Status is required")
    private String status;
}