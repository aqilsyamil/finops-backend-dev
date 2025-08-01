package com.example.poc_finops.tagging.api;

import com.example.poc_finops.tagging.api.dto.TagDto;
import com.example.poc_finops.tagging.api.dto.TagStatusDto;
import com.example.poc_finops.tagging.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Tag Management", description = "APIs for managing resource tags")
public class TagController {
    
    private final TagService tagService;
    
    @PostMapping
    @Operation(summary = "Create a new tag")
    public ResponseEntity<TagDto> createTag(
            @Parameter(description = "CSP Connection ID") @RequestParam UUID cspConnectionId,
            @Parameter(description = "Resource name") @RequestParam String resourceName,
            @Parameter(description = "Service name") @RequestParam String serviceName,
            @Parameter(description = "User ID") @RequestParam UUID userId) {
        
        TagDto tagDto = tagService.createTag(cspConnectionId, resourceName, serviceName, userId);
        return new ResponseEntity<>(tagDto, HttpStatus.CREATED);
    }
    
    @PutMapping("/{tagId}")
    @Operation(summary = "Update an existing tag")
    public ResponseEntity<TagDto> updateTag(
            @Parameter(description = "Tag ID") @PathVariable UUID tagId,
            @Parameter(description = "Resource name") @RequestParam String resourceName,
            @Parameter(description = "Service name") @RequestParam String serviceName,
            @Parameter(description = "User ID") @RequestParam UUID userId) {
        
        TagDto tagDto = tagService.updateTag(tagId, resourceName, serviceName, userId);
        return ResponseEntity.ok(tagDto);
    }
    
    @DeleteMapping("/{tagId}")
    @Operation(summary = "Delete a tag")
    public ResponseEntity<Void> deleteTag(@Parameter(description = "Tag ID") @PathVariable UUID tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{tagId}")
    @Operation(summary = "Get tag by ID")
    public ResponseEntity<TagDto> getTagById(@Parameter(description = "Tag ID") @PathVariable UUID tagId) {
        TagDto tagDto = tagService.getTagById(tagId);
        return ResponseEntity.ok(tagDto);
    }
    
    @GetMapping("/csp-connection/{cspConnectionId}")
    @Operation(summary = "Get tags by CSP connection")
    public ResponseEntity<List<TagDto>> getTagsByCspConnection(
            @Parameter(description = "CSP Connection ID") @PathVariable UUID cspConnectionId) {
        List<TagDto> tags = tagService.getTagsByCspConnection(cspConnectionId);
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/service/{serviceName}")
    @Operation(summary = "Get tags by service name")
    public ResponseEntity<List<TagDto>> getTagsByServiceName(
            @Parameter(description = "Service name") @PathVariable String serviceName) {
        List<TagDto> tags = tagService.getTagsByServiceName(serviceName);
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/service-id/{serviceId}")
    @Operation(summary = "Get tags by service ID")
    public ResponseEntity<List<TagDto>> getTagsByServiceId(
            @Parameter(description = "Service ID") @PathVariable UUID serviceId) {
        List<TagDto> tags = tagService.getTagsByServiceId(serviceId);
        return ResponseEntity.ok(tags);
    }
    
    @PostMapping("/{tagId}/status")
    @Operation(summary = "Add tag status (key-value pair)")
    public ResponseEntity<TagStatusDto> addTagStatus(
            @Parameter(description = "Tag ID") @PathVariable UUID tagId,
            @Parameter(description = "Tag name") @RequestParam String name,
            @Parameter(description = "Tag value") @RequestParam String value,
            @Parameter(description = "User ID") @RequestParam UUID userId) {
        
        TagStatusDto tagStatusDto = tagService.addTagStatus(tagId, name, value, userId);
        return new ResponseEntity<>(tagStatusDto, HttpStatus.CREATED);
    }
    
    @PutMapping("/status/{tagStatusId}")
    @Operation(summary = "Update tag status")
    public ResponseEntity<TagStatusDto> updateTagStatus(
            @Parameter(description = "Tag Status ID") @PathVariable UUID tagStatusId,
            @Parameter(description = "Tag name") @RequestParam String name,
            @Parameter(description = "Tag value") @RequestParam String value,
            @Parameter(description = "User ID") @RequestParam UUID userId) {
        
        TagStatusDto tagStatusDto = tagService.updateTagStatus(tagStatusId, name, value, userId);
        return ResponseEntity.ok(tagStatusDto);
    }
    
    @DeleteMapping("/status/{tagStatusId}")
    @Operation(summary = "Delete tag status")
    public ResponseEntity<Void> deleteTagStatus(
            @Parameter(description = "Tag Status ID") @PathVariable UUID tagStatusId) {
        tagService.deleteTagStatus(tagStatusId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{tagId}/status")
    @Operation(summary = "Get tag statuses by tag ID")
    public ResponseEntity<List<TagStatusDto>> getTagStatusesByTag(
            @Parameter(description = "Tag ID") @PathVariable UUID tagId) {
        List<TagStatusDto> tagStatuses = tagService.getTagStatusesByTag(tagId);
        return ResponseEntity.ok(tagStatuses);
    }
}