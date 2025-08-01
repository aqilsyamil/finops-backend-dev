package com.example.poc_finops.organization.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class TeamMemberId implements Serializable {
    private UUID team;
    private UUID user;
}