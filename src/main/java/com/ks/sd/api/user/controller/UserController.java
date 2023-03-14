package com.ks.sd.api.user.controller;

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

import com.ks.sd.api.user.dto.UserView;
import com.ks.sd.api.role.dto.RoleView;
import com.ks.sd.api.team.dto.TeamView;
import com.ks.sd.api.team.service.TeamService;
import com.ks.sd.api.user.dto.UserSaveRequest;
import com.ks.sd.api.user.dto.UserSaveResponse;
import com.ks.sd.api.user.dto.UserUpdateRequest;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.api.user.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    /**
     * 모든 사용자 조회
     * @return
     */
    @GetMapping
    public ResponseEntity<List<UserView>> getAllUsers() {
        List<UserView> allUsersList = userService.getAllUsers();
        return ResponseEntity.ok(allUsersList);
    }

    /**
     * 사용자 아이디로 사용자 조회
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserView> getUserByUserId(@PathVariable String userId) {
        User user = userService.getUserByUserId(userId);
        UserView userView = UserView.builder().user(user).build();
        return ResponseEntity.ok(userView);
    }

    /**
     * 삭제되지 않은 사용자 조회
     * @return
     */
    @GetMapping("/undeleted")
    public ResponseEntity<List<UserView>> getUndeletedUsers() {
        final String UNDELETED = "N";
        List<UserView> undeletedUsers = userService.getUsersByDelYn(UNDELETED);
        return ResponseEntity.ok(undeletedUsers);
    }

    /**
     * 삭제된 사용자 조회
     * @return
     */
    @GetMapping("/deleted")
    public ResponseEntity<List<UserView>> getDeletedUsers() {
        final String DELETED = "Y";
        List<UserView> deletedUsers = userService.getUsersByDelYn(DELETED);
        return ResponseEntity.ok(deletedUsers);
    }

    /**
     * 사용자 등록
     * @param userSaveRequest
     * @return
     */
    @PostMapping
    public ResponseEntity<UserSaveResponse> createUser(@RequestBody UserSaveRequest userSaveRequest) {
        User saveUser = userService.saveUser(userSaveRequest);
        User user = userService.getUserByUserId(saveUser.getUserId());
        UserSaveResponse userSaveResponse = UserSaveResponse.builder().user(user).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(userSaveResponse);
    }

    /**
     * 사용자 수정
     * @param userId
     * @param userUpdateRequest
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserSaveResponse> updateUser(
        @PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest
    ) {
        User user = userService.updateUser(userId, userUpdateRequest);
        UserSaveResponse userSaveResponse = UserSaveResponse.builder().user(user).build();
        return ResponseEntity.ok(userSaveResponse);
    }

    /**
     * 사용자 삭제
     * @param userId
     * @return
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 사용자의 팀 조회
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/team")
    public ResponseEntity<TeamView> getTeamByUserId(@PathVariable String userId) {
        TeamView teamView = teamService.getTeamByUserId(userId);
        return ResponseEntity.ok(teamView);
    }

    /**
     * 사용자의 권한 조회
     * @param userId
     * @return
     */
    @GetMapping("/{userId}/roles")
    public ResponseEntity<List<RoleView>> getRolesByUserId(@PathVariable String userId) {
        List<RoleView> roleViews = userService.getRolesByUserId(userId);
        return ResponseEntity.ok(roleViews);
    }

    /**
     * 사용자의 권한 삭제
     * @param userId
     * @param roleId
     * @return
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> deleteRoleByUserId(
        @PathVariable String userId, @PathVariable String roleId
    ) {
        userService.deleteRoleByUserId(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
