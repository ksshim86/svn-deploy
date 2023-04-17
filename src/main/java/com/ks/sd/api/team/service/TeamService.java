package com.ks.sd.api.team.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.team.dto.TeamSaveRequest;
import com.ks.sd.api.team.dto.TeamUpdateRequest;
import com.ks.sd.api.team.dto.TeamResponse;
import com.ks.sd.api.team.entity.Team;
import com.ks.sd.api.team.repository.TeamRepository;
import com.ks.sd.api.user.dto.UserResponse;
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
     * 모든 팀을 조회합니다.
     * @return 모든 팀 목록 {@link TeamResponse}
     */
    public List<TeamResponse> getAllTeams() {
        return 
            teamRepository.findAll().stream()
                .map(team -> TeamResponse.builder().team(team).build())
                .collect(Collectors.toList());
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
     * 팀을 저장합니다.
     * @param teamSaveRequest 팀 저장 요청
     * @return 저장된 팀 {@link TeamResponse}
     */
    public TeamResponse saveTeam(TeamSaveRequest teamSaveRequest) {
        Team team = teamRepository.save(teamSaveRequest.toEntity());
        return TeamResponse.builder().team(team).build();
    }

    /**
     * 팀을 수정합니다.
     * @param teamUpdateRequest 팀 수정 요청
     * @return 수정된 팀 {@link TeamResponse}
     */
    public TeamResponse updateTeam(TeamUpdateRequest teamUpdateRequest) {
        Team team = this.getTeamByTeamNo(teamUpdateRequest.getTeamNo());
        team.update(teamUpdateRequest);
        
        return TeamResponse.builder().team(team).build();
    }

    /**
     * 팀을 삭제합니다.
     * @param teamNo 팀 번호
     */
    public void deleteTeam(Integer teamNo) {
        Team team = this.getTeamByTeamNo(teamNo);
        team.delete();
    }

    /**
     * 팀에 속한 사용자를 조회합니다.
     * @param teamNo 팀 번호
     * @return 팀에 속한 사용자 목록 {@link UserResponse}
     */
    public List<UserResponse> getUsersByTeamNo(Integer teamNo) {
        Team team = this.getTeamByTeamNo(teamNo);

        List<UserResponse> responses = 
            team.getUsers().stream()
                .map(user -> UserResponse.builder().user(user).build())
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * 팀에 사용자를 추가합니다.
     * @param teamNo 팀 번호
     * @param userId 사용자 아이디
     */
    public void addUserToTeam(Integer teamNo, String userId) {
        Team team = getTeamByTeamNo(teamNo);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.setTeam(team);
    }

    /**
     * 팀에서 사용자를 제외합니다.
     * @param teamNo 팀 번호
     * @param userId 사용자 아이디
     */
    public void removeUserFromTeam(Integer teamNo, String userId) {
        Team team = teamRepository.findById(teamNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.TEAM_NOT_FOUND));
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        if (!team.equals(user.getTeam())) {
            throw new BusinessException(ErrorCode.USER_NOT_IN_TEAM);
        }
    
        user.setTeam(null);
    }
}
