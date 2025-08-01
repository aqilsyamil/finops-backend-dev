package com.example.poc_finops.organization.config;

import com.example.poc_finops.organization.api.dto.CreateUserRequest;
import com.example.poc_finops.organization.api.dto.UpdateRoleRequest;
import com.example.poc_finops.organization.domain.valueobject.UserStatus;
import com.example.poc_finops.organization.service.OrganizationService;
import com.example.poc_finops.organization.service.RoleService;
import com.example.poc_finops.organization.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrganizationService organizationService;
    private final RoleService roleService;
    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // Skip if data already exists
        if (!organizationService.getAllOrganizations().isEmpty()) {
            log.info("Data already initialized, skipping...");
            return;
        }

        log.info("Initializing test data...");

        // Create organization
        var organization = organizationService.createOrganization("Test Organization");
        log.info("Created organization: {}", organization.getOrganizationName());

        // Create roles
        var adminRole = roleService.createRole(new UpdateRoleRequest() {{
            setRoleName("Admin");
            setRoleType("system");
            setStatus("active");
        }});
        
        var userRole = roleService.createRole(new UpdateRoleRequest() {{
            setRoleName("User");
            setRoleType("system");
            setStatus("active");
        }});
        
        log.info("Created roles: Admin, User");

        // Create test users
        var adminUser = userService.createUser(new CreateUserRequest() {{
            setUsername("admin");
            setEmail("admin@test.com");
            setPassword("admin123");
            setOrganizationId(organization.getId());
            setRoleId(adminRole.getId());
        }});
        
        var testUser = userService.createUser(new CreateUserRequest() {{
            setUsername("testuser");
            setEmail("user@test.com");
            setPassword("user123");
            setOrganizationId(organization.getId());
            setRoleId(userRole.getId());
        }});

        log.info("Created test users: admin@test.com (admin123), user@test.com (user123)");
        log.info("Data initialization completed!");
    }
}