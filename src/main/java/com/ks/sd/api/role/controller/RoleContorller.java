package com.ks.sd.api.role.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.role.dto.RoleUpdateRequest;
import com.ks.sd.api.role.dto.RoleUpdateResponse;
import com.ks.sd.api.role.dto.RoleView;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.service.RoleService;
import com.ks.sd.api.user.dto.UserView;

@RestController
@RequestMapping("/api/role")
public class RoleContorller {
    @Autowired
    private RoleService roleService;

    /**
     * 모든 권한 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<List<RoleView>> getAllRoles() {
        List<RoleView> roleViews = roleService.getAllRoles();

        return ResponseEntity.ok(roleViews);
    }

    /**
     * 권한 수정
     * @param roleCd
     * @param roleUpdateRequest
     * @return
     */
    @PutMapping("/{roleCd}")
    public ResponseEntity<RoleUpdateResponse> updateRole(
        @PathVariable String roleCd, @RequestBody RoleUpdateRequest roleUpdateRequest
    ) {
        Role role = roleService.updateRole(roleCd, roleUpdateRequest);
        RoleUpdateResponse roleUpdateResponse = RoleUpdateResponse.builder().role(role).build();
        return ResponseEntity.ok(roleUpdateResponse);
    }

    /**
     * 권한에 속한 사용자 조회
     * @param roleCd
     * @return
     */
    @GetMapping("/{roleCd}/users")
    public ResponseEntity<List<UserView>> getUsersByRole(@PathVariable String roleCd) {
        List<UserView> userViews = roleService.getUsersByRole(roleCd);
        return ResponseEntity.ok(userViews);
    }

    /**
     * 권한에 사용자 추가
     * @param roleCd
     * @param userId
     * @return
     */
    @PostMapping("/{roleCd}/users/{userId}")
    public ResponseEntity<Void> addUserToRole(
        @PathVariable String roleCd, @PathVariable String userId
    ) {
        roleService.addUserToRole(roleCd, userId);
        return ResponseEntity.ok().build();
    }

    /**
     * 권한에 사용자 삭제
     * @param roleCd
     * @param userId
     * @return
     */
    @DeleteMapping("/{roleCd}/users/{userId}")
    public ResponseEntity<Void> deleteUserFromRole(
        @PathVariable String roleCd, @PathVariable String userId
    ) {
        roleService.deleteUserFromRole(roleCd, userId);
        return ResponseEntity.ok().build();
    }
}
