package com.ks.sd.api.team.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.team.dto.TeamSaveRequest;
import com.ks.sd.api.team.dto.TeamUpdateRequest;
import com.ks.sd.api.team.dto.TeamResponse;
import com.ks.sd.api.team.service.TeamService;
import com.ks.sd.api.user.dto.UserResponse;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @GetMapping
    public List<TeamResponse> getAllTeams() {
        return teamService.getAllTeams();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamResponse saveTeam(@RequestBody TeamSaveRequest teamSaveRequest) {
        return teamService.saveTeam(teamSaveRequest);
    }

    @PutMapping("/{teamNo}")
    public TeamResponse updateTeam(
        @PathVariable Integer teamNo, @RequestBody TeamUpdateRequest teamUpdateRequest
    ) {
        teamUpdateRequest.setTeamNo(teamNo);
        return teamService.updateTeam(teamUpdateRequest);
    }

    @DeleteMapping("/{teamNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTeam(@PathVariable Integer teamNo) {
        teamService.deleteTeam(teamNo);
    }

    @GetMapping("/{teamNo}/users")
    public List<UserResponse> getUsersByTeamNo(@PathVariable Integer teamNo) {
        return teamService.getUsersByTeamNo(teamNo);
    }

    @PostMapping("/{teamNo}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserToTeam(
        @PathVariable("teamNo") Integer teamNo, @PathVariable("userId") String userId
    ) {
        teamService.addUserToTeam(teamNo, userId);
    }

    @DeleteMapping("/{teamNo}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserFromTeam(@PathVariable Integer teamNo, @PathVariable String userId) {
        teamService.removeUserFromTeam(teamNo, userId);
    }
}
