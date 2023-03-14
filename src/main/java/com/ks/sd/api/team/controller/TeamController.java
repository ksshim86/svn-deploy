package com.ks.sd.api.team.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.team.dto.TeamSaveRequest;
import com.ks.sd.api.team.dto.TeamSaveResponse;
import com.ks.sd.api.team.dto.TeamUpdateRequest;
import com.ks.sd.api.team.dto.TeamView;
import com.ks.sd.api.team.entity.Team;
import com.ks.sd.api.team.service.TeamService;
import com.ks.sd.api.user.dto.UserView;
import com.ks.sd.api.user.service.UserService;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    /**
     * 모든 팀 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<List<TeamView>> getAllTeams() {
        List<TeamView> teamViews = teamService.getAllTeams();

        return ResponseEntity.ok(teamViews);
    }

    /**
     * 삭제되지 않은 팀 조회
     * @return
     */
    @GetMapping("/undeleted")
    public ResponseEntity<List<TeamView>> getUndeletedTeams() {
        final String UNDELETED = "N";
        List<TeamView> undeletedTeams = teamService.getTeamsByDelYn(UNDELETED);
        return ResponseEntity.ok(undeletedTeams);
    }

    /**
     * 삭제된 팀 조회
     * @return
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<TeamView>> getDeletedTeams() {
        final String DELETED = "Y";
        List<TeamView> deletedTeams = teamService.getTeamsByDelYn(DELETED);
        return ResponseEntity.ok(deletedTeams);
    }

    /**
     * 팀 등록
     * @param teamSaveRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<TeamSaveResponse> createTeam(@RequestBody TeamSaveRequest teamSaveRequest) {
        Team saveTeam = teamService.saveTeam(teamSaveRequest);
        Team team = teamService.getTeamByTeamNo(saveTeam.getTeamNo());
        TeamSaveResponse teamSaveResponse = TeamSaveResponse.builder().team(team).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(teamSaveResponse);
    }

    /**
     * 팀 수정
     * @param teamNo
     * @param teamUpdateRequest
     * @return
     */
    @PutMapping("/{teamNo}")
    public ResponseEntity<TeamSaveResponse> updateTeam(
        @PathVariable Integer teamNo, @RequestBody TeamUpdateRequest teamUpdateRequest
    ) {
        Team updateTeam = teamService.updateTeam(teamNo, teamUpdateRequest);
        TeamSaveResponse teamSaveResponse = TeamSaveResponse.builder().team(updateTeam).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(teamSaveResponse);
    }

    /**
     * 팀 삭제
     * @param teamNo
     * @return
     */
    @DeleteMapping("/{teamNo}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Integer teamNo) {
        teamService.deleteTeam(teamNo);

        return ResponseEntity.noContent().build();
    }

    /**
     * 팀에 속한 사용자 조회
     * @param teamNo
     * @return
     */
    @GetMapping("/{teamNo}/users")
    public ResponseEntity<List<UserView>> getUsersByTeam(@PathVariable Integer teamNo) {
        List<UserView> userViewList = userService.getUsersByTeam(teamNo);

        return ResponseEntity.ok(userViewList);
    }

    /**
     * 팀에 사용자 추가
     * @param teamNo
     * @param userId
     * @return
     */
    @PostMapping("/{teamNo}/users/{userId}")
    public ResponseEntity<TeamView> addUserToTeam(
        @PathVariable("teamNo") Integer teamNo, @PathVariable("userId") String userId
    ) {
        teamService.addUserToTeam(teamNo, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 팀에서 사용자 제외
     * @param teamNo
     * @param userId
     * @return
     */
    @DeleteMapping("/{teamNo}/users/{userId}")
    public ResponseEntity<Void> excludeUserFromTeam(@PathVariable Integer teamNo, @PathVariable String userId) {
        teamService.excludeUserFromTeam(teamNo, userId);
        return ResponseEntity.ok().build();
    }
}
