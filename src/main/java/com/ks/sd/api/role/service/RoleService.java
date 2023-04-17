package com.ks.sd.api.role.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.role.dto.RoleUpdateRequest;
import com.ks.sd.api.role.dto.RoleResponse;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.repository.RoleRepository;
import com.ks.sd.api.user.dto.UserResponse;
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
     * 모든 권한을 조회합니다.
     * @return 모든 권한 목록 {@link RoleResponse}
     */
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = 
            roleRepository.findAllByOrderByRoleLvlAsc()
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        List<RoleResponse> responses = roles.stream()
            .map(role -> RoleResponse.builder().role(role).build())
            .collect(Collectors.toList());

        return responses;
    }

    /**
     * 권한코드에 해당하는 권한을 조회합니다.
     * @param roleCd 권한코드
     * @return 권한 {@link Role}
     */
    public Role getRoleByRoleCd(String roleCd) {
        return 
            roleRepository.findByRoleCd(roleCd)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }

    /**
     * 권한을 수정합니다.
     * @param roleUpdateRequest
     * @return 수정된 권한 {@link RoleResponse}
     */
    public RoleResponse updateRole(RoleUpdateRequest roleUpdateRequest) {
        Role role = this.getRoleByRoleCd(roleUpdateRequest.getRoleCd());
        role.update(roleUpdateRequest);

        return RoleResponse.builder().role(role).build();
    }

    /**
     * 권한에 속한 사용자들을 조회합니다.
     * @param roleCd 권한코드
     * @return 사용자 목록 {@link UserResponse}
     */
    public List<UserResponse> getUsersByRole(String roleCd) {
        Role role = this.getRoleByRoleCd(roleCd);
        List<UserRole> userRoles = role.getUserRoles();

        return 
            userRoles.stream()
                .map(userRole -> UserResponse.builder().user(userRole.getUser()).build())
                .collect(Collectors.toList());
    }

    /**
     * 권한에 사용자를 추가합니다.
     * @param roleCd 권한코드
     * @param userId 사용자 아이디
     */
    public void addUserToRole(String roleCd, String userId) {
        Role role = this.getRoleByRoleCd(roleCd);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        boolean alreadyHasRole = user.getUserRoles().stream().anyMatch(ur -> ur.getRole().equals(role));

        if (alreadyHasRole) {
            throw new BusinessException(ErrorCode.USER_ALREADY_HAS_ROLE);
        }

        UserRole userRole = UserRole.builder().user(user).role(role).build();

        userRoleRepository.save(userRole);
    }

    /**
     * 권한에 속한 사용자를 제외합니다.
     * @param roleCd 권한코드
     * @param userId 사용자 아이디
     */
    public void removeUserFromRole(String roleCd, String userId) {
        Role role = this.getRoleByRoleCd(roleCd);
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        UserRole userRole = user.getUserRoles().stream()
            .filter(ur -> ur.getRole().equals(role))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_HAS_ROLE));

        userRoleRepository.delete(userRole);
    }
}
