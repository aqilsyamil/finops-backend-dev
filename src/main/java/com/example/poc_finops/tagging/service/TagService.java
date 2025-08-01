package com.example.poc_finops.tagging.service;

import com.example.poc_finops.tagging.api.dto.TagDto;
import com.example.poc_finops.tagging.api.dto.TagStatusDto;

import java.util.List;
import java.util.UUID;

public interface TagService {
    
    TagDto createTag(UUID cspConnectionId, String resourceName, String serviceName, UUID userId);
    
    TagDto updateTag(UUID tagId, String resourceName, String serviceName, UUID userId);
    
    void deleteTag(UUID tagId);
    
    TagDto getTagById(UUID tagId);
    
    List<TagDto> getTagsByCspConnection(UUID cspConnectionId);
    
    List<TagDto> getTagsByServiceName(String serviceName);
    
    List<TagDto> getTagsByServiceId(UUID serviceId);
    
    TagStatusDto addTagStatus(UUID tagId, String name, String value, UUID userId);
    
    TagStatusDto updateTagStatus(UUID tagStatusId, String name, String value, UUID userId);
    
    void deleteTagStatus(UUID tagStatusId);
    
    List<TagStatusDto> getTagStatusesByTag(UUID tagId);
}