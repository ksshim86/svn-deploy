package com.ks.sd.api.role.controller;

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

import com.ks.sd.api.role.dto.RoleUpdateRequest;
import com.ks.sd.api.role.dto.RoleResponse;
import com.ks.sd.api.role.service.RoleService;
import com.ks.sd.api.user.dto.UserResponse;

@RestController
@RequestMapping("/api/roles")
public class RoleContorller {
    @Autowired
    private RoleService roleService;

    @GetMapping
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PutMapping("/{roleCd}")
    public RoleResponse updateRole(
        @PathVariable String roleCd, @RequestBody RoleUpdateRequest roleUpdateRequest
    ) {
        roleUpdateRequest.setRoleCd(roleCd);

        return roleService.updateRole(roleUpdateRequest);
    }

    @GetMapping("/{roleCd}/users")
    public List<UserResponse> getUsersByRole(@PathVariable String roleCd) {
        return roleService.getUsersByRole(roleCd);
    }

    @PostMapping("/{roleCd}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUserToRole(
        @PathVariable String roleCd, @PathVariable String userId
    ) {
        roleService.addUserToRole(roleCd, userId);
    }

    @DeleteMapping("/{roleCd}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserFromRole(
        @PathVariable String roleCd, @PathVariable String userId
    ) {
        roleService.removeUserFromRole(roleCd, userId);
    }
}
