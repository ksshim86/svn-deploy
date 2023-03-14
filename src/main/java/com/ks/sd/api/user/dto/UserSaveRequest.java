package com.ks.sd.api.user.dto;

import com.ks.sd.api.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSaveRequest {
    private String userId;
    private String userNm;
    private String password;

    public User toEntity() {
        return User.builder()
            .userId(userId)
            .userNm(userNm)
            .password(password).build();
    }
}
