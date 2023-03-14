package com.ks.sd.api.role.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.role.dto.RoleUpdateRequest;
import com.ks.sd.api.role.dto.RoleView;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.repository.RoleRepository;
import com.ks.sd.api.user.dto.UserView;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.api.user.entity.UserRole;
import com.ks.sd.api.user.repository.UserRepository;
import com.ks.sd.api.user.repository.UserRoleRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    /**
     * 모든 권한 조회
     * @return
     */
    public List<RoleView> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        List<RoleView> roleViews = roles.stream()
            .map(role -> RoleView.builder().role(role).build())
            .collect(Collectors.toList());

        return roleViews;
    }

    /**
     * 권한코드로 권한 조회
     * @param roleCd
     * @return
     */
    public Role getRoleByRoleCd(String roleCd) {
        Role role = roleRepository.findByRoleCd(roleCd).orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        return role;
    }

    /**
     * 권한 수정
     * @param roleCd
     * @param roleUpdateRequest
     * @return
     */
    public Role updateRole(String roleCd, RoleUpdateRequest roleUpdateRequest) {
        Role role = this.getRoleByRoleCd(roleCd);
        role.update(roleUpdateRequest);

        return roleRepository.save(role);
    }

    /**
     * 권한에 속한 사용자 조회
     * @param roleCd
     * @return
     */
    public List<UserView> getUsersByRole(String roleCd) {
        Role role = this.getRoleByRoleCd(roleCd);
        List<UserRole> userRoles = role.getUserRoles();

        List<UserView> roleViews = userRoles.stream()
                .map(userRole -> UserView.builder().user(userRole.getUser()).build())
                .filter(userView -> !userView.isDeleted())
                .collect(Collectors.toList());

        return roleViews;
    }

    /**
     * 권한에 사용자 추가
     * @param roleCd
     * @param userId
     */
    public void addUserToRole(String roleCd, String userId) {
        Role role = this.getRoleByRoleCd(roleCd);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        List<UserRole> userRoles = user.getUserRoles();
        boolean alreadyHasRole = userRoles.stream().anyMatch(ur -> ur.getRole().equals(role));

        if (alreadyHasRole) {
            throw new BusinessException(ErrorCode.USER_ALREADY_HAS_ROLE);
        }

        UserRole userRole = UserRole.builder().user(user).role(role).build();

        userRoleRepository.save(userRole);
    }

    /**
     * 권한에 사용자 삭제
     * @param roleCd
     * @param userId
     */
    public void deleteUserFromRole(String roleCd, String userId) {
        Role role = this.getRoleByRoleCd(roleCd);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
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
