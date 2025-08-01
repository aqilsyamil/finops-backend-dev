package com.example.poc_finops.organization.service;

import com.example.poc_finops.organization.domain.entity.Team;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<Team> getAllTeams();
    Team getTeamById(UUID teamId);
    Team createTeam(String teamName, String status);
    Team updateTeam(UUID teamId, String teamName, String status);
    void deleteTeam(UUID teamId);
}