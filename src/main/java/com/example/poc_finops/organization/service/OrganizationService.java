package com.example.poc_finops.organization.service;

import com.example.poc_finops.organization.api.dto.OrganizationDto;
import com.example.poc_finops.organization.domain.entity.Organization;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<OrganizationDto> getAllOrganizations();
    OrganizationDto getOrganizationById(UUID id);
    OrganizationDto createOrganization(String organizationName);
    OrganizationDto updateOrganization(UUID id, String organizationName);
    void deleteOrganization(UUID id);
    Organization getOrganizationEntityById(UUID id);
}