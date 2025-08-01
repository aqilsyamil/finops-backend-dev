package com.example.poc_finops.organization.repository;

import com.example.poc_finops.organization.domain.entity.TeamMember;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
}