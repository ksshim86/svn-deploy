package com.ks.sd.api.team.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.team.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByDelYn(String delYn);
}
