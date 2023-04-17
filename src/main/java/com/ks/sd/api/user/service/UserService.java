package com.ks.sd.api.user.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.user.dto.UserResponse;
import com.ks.sd.api.role.dto.RoleResponse;
import com.ks.sd.api.team.dto.TeamResponse;
import com.ks.sd.api.user.dto.UserSaveRequest;
import com.ks.sd.api.user.dto.UserUpdateRequest;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.api.user.repository.UserRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * 모든 사용자를 조회합니다.
     * @return 모든 사용자 목록 {@link UserResponse}
     */
    public List<UserResponse> getAllUsers() {
        return 
            userRepository.findAll().stream()
                .map(user -> UserResponse.builder().user(user).build())
                .collect(Collectors.toList());
    }

    /**
     * 사용자 아이디로 사용자를 조회합니다.
     * @param userId 사용자 아이디
     * @return 사용자 {@link User}
     */
    public User getUserByUserId(String userId) {
        return 
            userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 아이디로 사용자를 조회합니다.
     * @param userId 사용자 아이디
     * @return 사용자 {@link UserResponse}
     */
    public UserResponse getUserResponseByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder().user(user).build();
    }

    /**
     * 사용자를 등록합니다.
     * @param userSaveRequest 사용자 등록 요청
     * @return 등록된 사용자 {@link UserResponse}
     */
    public UserResponse saveUser(UserSaveRequest userSaveRequest) {
        userRepository.findByUserId(userSaveRequest.getUserId())
            .ifPresent(user -> {
                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
            });

        // 비밀번호 초기화, BCryptPasswordEncoder 사용하여 암호화 개발 필요
        userSaveRequest.setPassword("1234");
        User saveUser = userRepository.save(userSaveRequest.toEntity());

        return UserResponse.builder().user(saveUser).build();
    }

    /**
     * 사용자를 수정합니다.
     * @param userUpdateRequest 사용자 수정 요청
     * @return 수정된 사용자 {@link UserResponse}
     */
    public UserResponse updateUser(UserUpdateRequest userUpdateRequest) {
        User user = this.getUserByUserId(userUpdateRequest.getUserId());
        user.update(userUpdateRequest);
        
        return UserResponse.builder().user(user).build();
    }

    /**
     * 사용자를 삭제합니다.
     * @param userId 사용자 아이디
     */
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.delete();
    }

    /**
     * 사용자의 팀을 조회합니다.
     * @param userId 사용자 아이디
     * @return 사용자의 팀 {@link TeamResponse}
     */
    public TeamResponse getTeamByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return TeamResponse.builder().team(user.getTeam()).build();
    }

    /**
     * 사용자의 권한들을 조회합니다.
     * @param userId 사용자 아이디
     * @return 사용자의 권한 목록 {@link RoleResponse}
     */
    public List<RoleResponse> getRolesByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<RoleResponse> responses = user.getUserRoles().stream()
            .map(userRole -> RoleResponse.builder().role(userRole.getRole()).build())
            .collect(Collectors.toList());

        return responses;
    }
}
