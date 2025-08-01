package com.example.poc_finops.organization.service.impl;

import com.example.poc_finops.organization.domain.entity.Team;
import com.example.poc_finops.organization.repository.TeamRepository;
import com.example.poc_finops.organization.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeamById(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
    }

    @Override
    public Team createTeam(String teamName, String status) {
        Team team = new Team();
        team.setTeamName(teamName);
        team.setStatus(status);
        return teamRepository.save(team);
    }

    @Override
    public Team updateTeam(UUID teamId, String teamName, String status) {
        Team team = getTeamById(teamId);
        team.setTeamName(teamName);
        team.setStatus(status);
        return teamRepository.save(team);
    }

    @Override
    public void deleteTeam(UUID teamId) {
        if (!teamRepository.existsById(teamId)) {
            throw new RuntimeException("Team not found with id: " + teamId);
        }
        teamRepository.deleteById(teamId);
    }
}