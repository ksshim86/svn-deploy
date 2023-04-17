package com.ks.sd.api.user.controller;

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

import com.ks.sd.api.user.dto.UserResponse;
import com.ks.sd.api.role.dto.RoleResponse;
import com.ks.sd.api.team.dto.TeamResponse;
import com.ks.sd.api.user.dto.UserSaveRequest;
import com.ks.sd.api.user.dto.UserUpdateRequest;
import com.ks.sd.api.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserResponse getUserByUserId(@PathVariable String userId) {
        return userService.getUserResponseByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse saveUser(@RequestBody UserSaveRequest userSaveRequest) {
        return userService.saveUser(userSaveRequest);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(
        @PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        userUpdateRequest.setUserId(userId);
        return userService.updateUser(userUpdateRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }
    
    @GetMapping("/{userId}/team")
    public TeamResponse getTeamByUserId(@PathVariable String userId) {
        return userService.getTeamByUserId(userId);
    }

    @GetMapping("/{userId}/roles")
    public List<RoleResponse> getRolesByUserId(@PathVariable String userId) {
        return userService.getRolesByUserId(userId);
    }
}
