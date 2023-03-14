package com.ks.sd.api.team.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.team.dto.TeamSaveRequest;
import com.ks.sd.api.team.dto.TeamUpdateRequest;
import com.ks.sd.api.team.dto.TeamView;
import com.ks.sd.api.team.entity.Team;
import com.ks.sd.api.team.repository.TeamRepository;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.api.user.repository.UserRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 모든 팀 조회
     * @return
     */
    public List<TeamView> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        List<TeamView> allTeamsList = teams.stream()
            .map(team -> TeamView.builder().team(team).build())
            .collect(Collectors.toList());
        
        return allTeamsList;
    }

    /**
     * 삭제여부로 팀 조회
     * @param delYn
     * @return
     */
    public List<TeamView> getTeamsByDelYn(String delYn) {
        List<Team> teams = teamRepository.findByDelYn(delYn);
        
        List<TeamView> allTeamsList = teams.stream()
                .map(team -> TeamView.builder().team(team).build())
                .collect(Collectors.toList());
        
        return allTeamsList;
    }

    /**
     * 팀 번호로 팀 조회
     * @param teamNo
     * @return
     */
    public Team getTeamByTeamNo(Integer teamNo) {
        Team team = teamRepository.findById(teamNo).orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
        
        return team;
    }

    /**
     * 팀 저장
     * @param teamSaveRequest
     * @return
     */
    public Team saveTeam(TeamSaveRequest teamSaveRequest) {
        return teamRepository.save(teamSaveRequest.toEntity());
    }

    /**
     * 팀 수정
     * @param teamNo
     * @param teamUpdateRequest
     * @return
     */
    public Team updateTeam(Integer teamNo, TeamUpdateRequest teamUpdateRequest) {
        Team team = this.getTeamByTeamNo(teamNo);
        team.update(teamUpdateRequest);
        
        return teamRepository.save(team);
    }

    /**
     * 팀 삭제
     * @param teamNo
     */
    public void deleteTeam(Integer teamNo) {
        Team team = this.getTeamByTeamNo(teamNo);
        
        team.deleteTeam();
        teamRepository.save(team);
    }

    /**
     * 팀에 사용자 추가
     * @param teamNo
     * @param userId
     */
    public void addUserToTeam(Integer teamNo, String userId) {
        Team team = getTeamByTeamNo(teamNo);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setTeam(team);
    }

    /**
     * 팀에서 사용자 제외
     * @param teamNo
     * @param userId
     */
    public void excludeUserFromTeam(Integer teamNo, String userId) {
        Team team = teamRepository.findById(teamNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        team.getUsers().remove(user);
        user.setTeam(null);
    }

    /**
     * 사용자의 팀 조회
     * @param userId
     * @return
     */
    public TeamView getTeamByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        Team team = user.getTeam();
        
        if (team == null) {
            throw new BusinessException(ErrorCode.TEAM_NOT_FOUND);
        }

        return TeamView.builder().team(team).build();
    }
}
