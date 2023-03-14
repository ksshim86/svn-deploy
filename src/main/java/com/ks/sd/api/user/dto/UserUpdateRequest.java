package com.ks.sd.api.user.dto;

import com.ks.sd.api.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String userNm;
    private String password;

    public User toEntity() {
        return User.builder()
            .userNm(userNm)
            .password(password).build();
    }
}
