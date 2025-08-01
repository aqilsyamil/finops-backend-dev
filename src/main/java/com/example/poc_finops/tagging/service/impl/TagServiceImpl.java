package com.example.poc_finops.tagging.service.impl;

import com.example.poc_finops.cloudconnections.domain.entity.CspConnection;
import com.example.poc_finops.cloudconnections.domain.entity.Services;
import com.example.poc_finops.cloudconnections.repository.CspConnectionRepository;
import com.example.poc_finops.cloudconnections.repository.ServicesRepository;
import com.example.poc_finops.organization.domain.entity.User;
import com.example.poc_finops.organization.repository.UserRepository;
import com.example.poc_finops.tagging.api.dto.TagDto;
import com.example.poc_finops.tagging.api.dto.TagStatusDto;
import com.example.poc_finops.tagging.domain.entity.Tags;
import com.example.poc_finops.tagging.domain.entity.TagStatus;
import com.example.poc_finops.tagging.repository.TagsRepository;
import com.example.poc_finops.tagging.repository.TagStatusRepository;
import com.example.poc_finops.tagging.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    
    private final TagsRepository tagsRepository;
    private final TagStatusRepository tagStatusRepository;
    private final CspConnectionRepository cspConnectionRepository;
    private final ServicesRepository servicesRepository;
    private final UserRepository userRepository;
    
    @Override
    public TagDto createTag(UUID cspConnectionId, String resourceName, String serviceName, UUID userId) {
        CspConnection cspConnection = cspConnectionRepository.findById(cspConnectionId)
            .orElseThrow(() -> new IllegalArgumentException("CSP Connection not found with ID: " + cspConnectionId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        Tags tag = new Tags();
        tag.setCspConnection(cspConnection);
        // For now, we'll find service by name, but this should ideally be changed to use serviceId
        if (serviceName != null) {
            List<Services> services = servicesRepository.findByServiceName(serviceName);
            if (!services.isEmpty()) {
                tag.setService(services.get(0)); // Take the first match
            }
        }
        tag.setCreatedBy(user);
        
        Tags savedTag = tagsRepository.save(tag);
        return TagDto.fromEntity(savedTag);
    }
    
    @Override
    public TagDto updateTag(UUID tagId, String resourceName, String serviceName, UUID userId) {
        Tags tag = tagsRepository.findById(tagId)
            .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + tagId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        // For now, we'll find service by name, but this should ideally be changed to use serviceId
        if (serviceName != null) {
            List<Services> services = servicesRepository.findByServiceName(serviceName);
            if (!services.isEmpty()) {
                tag.setService(services.get(0)); // Take the first match
            }
        }
        tag.setUpdatedBy(user);
        
        Tags savedTag = tagsRepository.save(tag);
        return TagDto.fromEntity(savedTag);
    }
    
    @Override
    public void deleteTag(UUID tagId) {
        if (!tagsRepository.existsById(tagId)) {
            throw new IllegalArgumentException("Tag not found with ID: " + tagId);
        }
        tagsRepository.deleteById(tagId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TagDto getTagById(UUID tagId) {
        Tags tag = tagsRepository.findById(tagId)
            .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + tagId));
        return TagDto.fromEntity(tag);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getTagsByCspConnection(UUID cspConnectionId) {
        List<Tags> tags = tagsRepository.findByCspConnectionId(cspConnectionId);
        return tags.stream().map(TagDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getTagsByServiceName(String serviceName) {
        List<Tags> tags = tagsRepository.findByServiceName(serviceName);
        return tags.stream().map(TagDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagDto> getTagsByServiceId(UUID serviceId) {
        List<Tags> tags = tagsRepository.findByServiceId(serviceId);
        return tags.stream().map(TagDto::fromEntity).collect(Collectors.toList());
    }
    
    @Override
    public TagStatusDto addTagStatus(UUID tagId, String name, String value, UUID userId) {
        Tags tag = tagsRepository.findById(tagId)
            .orElseThrow(() -> new IllegalArgumentException("Tag not found with ID: " + tagId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        TagStatus tagStatus = new TagStatus();
        tagStatus.setTags(tag);
        tagStatus.setName(name);
        tagStatus.setValue(value);
        tagStatus.setCreatedBy(user);
        
        TagStatus savedTagStatus = tagStatusRepository.save(tagStatus);
        return TagStatusDto.fromEntity(savedTagStatus);
    }
    
    @Override
    public TagStatusDto updateTagStatus(UUID tagStatusId, String name, String value, UUID userId) {
        TagStatus tagStatus = tagStatusRepository.findById(tagStatusId)
            .orElseThrow(() -> new IllegalArgumentException("Tag status not found with ID: " + tagStatusId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        tagStatus.setName(name);
        tagStatus.setValue(value);
        tagStatus.setUpdatedBy(user);
        
        TagStatus savedTagStatus = tagStatusRepository.save(tagStatus);
        return TagStatusDto.fromEntity(savedTagStatus);
    }
    
    @Override
    public void deleteTagStatus(UUID tagStatusId) {
        if (!tagStatusRepository.existsById(tagStatusId)) {
            throw new IllegalArgumentException("Tag status not found with ID: " + tagStatusId);
        }
        tagStatusRepository.deleteById(tagStatusId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TagStatusDto> getTagStatusesByTag(UUID tagId) {
        List<TagStatus> tagStatuses = tagStatusRepository.findByTagsId(tagId);
        return tagStatuses.stream().map(TagStatusDto::fromEntity).collect(Collectors.toList());
    }
}