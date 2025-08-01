package com.example.poc_finops.tagging.domain.valueobject;

import lombok.Value;

@Value
public class TagKey {
    String value;
    
    public TagKey(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag key cannot be null or empty");
        }
        if (value.length() > 128) {
            throw new IllegalArgumentException("Tag key cannot exceed 128 characters");
        }
        this.value = value.trim();
    }
    
    public static TagKey of(String value) {
        return new TagKey(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}