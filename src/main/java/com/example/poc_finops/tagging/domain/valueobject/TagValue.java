package com.example.poc_finops.tagging.domain.valueobject;

import lombok.Value;

@Value
public class TagValue {
    String value;
    
    public TagValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Tag value cannot be null");
        }
        if (value.length() > 256) {
            throw new IllegalArgumentException("Tag value cannot exceed 256 characters");
        }
        this.value = value;
    }
    
    public static TagValue of(String value) {
        return new TagValue(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}