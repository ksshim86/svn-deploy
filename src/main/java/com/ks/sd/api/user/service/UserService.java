package com.ks.sd.api.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.user.dto.UserView;
import com.ks.sd.api.role.dto.RoleView;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.service.RoleService;
import com.ks.sd.api.user.dto.UserSaveRequest;
import com.ks.sd.api.user.dto.UserUpdateRequest;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.api.user.entity.UserRole;
import com.ks.sd.api.user.repository.UserRepository;
import com.ks.sd.api.user.repository.UserRoleRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 모든 사용자 조회
     * @return
     */
    public List<UserView> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserView> allUsersList = users.stream()
            .map(user -> UserView.builder().user(user).build())
            .collect(Collectors.toList());

        return allUsersList;
    }

    /**
     * 삭제여부로 사용자 조회
     * @param delYn
     * @return
     */
    public List<UserView> getUsersByDelYn(String delYn) {
        List<User> users = userRepository.findByDelYn(delYn).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<UserView> allUsersList = users.stream()
                .map(user -> UserView.builder().user(user).build())
                .collect(Collectors.toList());

        return allUsersList;
    }
    
    /**
     * 사용자 아이디로 사용자 조회
     * @param userId
     * @return
     */
    public User getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return user;
    }

    /**
     * 사용자 등록
     * @param userSaveRequest
     * @return
     */
    public User saveUser(UserSaveRequest userSaveRequest) {
        Optional<User> optionalUser = userRepository.findByUserId(userSaveRequest.getUserId());

        if (optionalUser.isPresent()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        } else {
            // 비밀번호 초기화, BCryptPasswordEncoder 사용하여 암호화 개발 필요
            userSaveRequest.setPassword("1234");
            
            return userRepository.save(userSaveRequest.toEntity());
        }
    }

    /**
     * 사용자 수정
     * @param userId
     * @param userUpdateRequest
     * @return
     */
    public User updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        User user = this.getUserByUserId(userId);

        user.update(userUpdateRequest);
        return userRepository.save(user);
    }

    /**
     * 사용자 삭제
     * @param userId
     */
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.deleteUser();
    }

    /**
     * 팀번호로 사용자 조회
     * @param teamNo
     * @return
     */
    public List<UserView> getUsersByTeam(Integer teamNo) {
        List<User> users =
            userRepository.findByTeamTeamNo(teamNo).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        List<UserView> userViewList = users.stream()
            .map(user -> UserView.builder().user(user).build())
            .collect(Collectors.toList());

        return userViewList;
    }

    /**
     * 사용자의 권한 조회
     * @param userId
     * @return
     */
    public List<RoleView> getRolesByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<UserRole> userRoles = user.getUserRoles();
        List<RoleView> roleViews = userRoles.stream()
            .map(userRole -> RoleView.builder().role(userRole.getRole()).build())
            .collect(Collectors.toList());

        return roleViews;
    }

    /**
     * 사용자의 권한 삭제
     * @param userId
     * @param roleCd
     */
    public void deleteRoleByUserId(String userId, String roleCd) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Role role = roleService.getRoleByRoleCd(roleCd);

        List<UserRole> userRoles = user.getUserRoles();
        boolean alreadyHasRole = userRoles.stream().anyMatch(ur -> ur.getRole().equals(role));

        if (!alreadyHasRole) {
            throw new BusinessException(ErrorCode.USER_NOT_HAS_ROLE);
        }

        UserRole userRole = userRoles.stream()
            .filter(ur -> ur.getRole().equals(role))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_HAS_ROLE));

        userRoleRepository.delete(userRole);
    }
}
