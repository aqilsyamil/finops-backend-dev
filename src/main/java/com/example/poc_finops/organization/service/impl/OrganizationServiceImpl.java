package com.example.poc_finops.organization.service.impl;

import com.example.poc_finops.organization.api.dto.OrganizationDto;
import com.example.poc_finops.organization.domain.entity.Organization;
import com.example.poc_finops.organization.repository.OrganizationRepository;
import com.example.poc_finops.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDto> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationDto getOrganizationById(UUID id) {
        Organization organization = getOrganizationEntityById(id);
        return convertToDto(organization);
    }

    @Override
    public OrganizationDto createOrganization(String organizationName) {
        Organization organization = new Organization();
        organization.setOrganizationName(organizationName);
        
        Organization savedOrganization = organizationRepository.save(organization);
        return convertToDto(savedOrganization);
    }

    @Override
    public OrganizationDto updateOrganization(UUID id, String organizationName) {
        Organization organization = getOrganizationEntityById(id);
        organization.setOrganizationName(organizationName);
        
        Organization updatedOrganization = organizationRepository.save(organization);
        return convertToDto(updatedOrganization);
    }

    @Override
    public void deleteOrganization(UUID id) {
        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found with id: " + id);
        }
        organizationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getOrganizationEntityById(UUID id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with id: " + id));
    }

    private OrganizationDto convertToDto(Organization organization) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getId());
        dto.setOrganizationName(organization.getOrganizationName());
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setUpdatedAt(organization.getUpdatedAt());
        return dto;
    }
}