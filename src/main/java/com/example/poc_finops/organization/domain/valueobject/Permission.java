package com.example.poc_finops.organization.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    private boolean canRead;
    private boolean canWrite;
    private boolean canList;
    private boolean canModify;
    
    public static Permission readOnly() {
        return new Permission(true, false, true, false);
    }
    
    public static Permission fullAccess() {
        return new Permission(true, true, true, true);
    }
    
    public static Permission noAccess() {
        return new Permission(false, false, false, false);
    }
}