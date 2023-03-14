package com.ks.sd.api.role.dto;

import com.ks.sd.api.role.entity.Role;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleUpdateResponse {
    private String roleCd;
    private String roleNm;

    @Builder
    public RoleUpdateResponse(Role role) {
        this.roleCd = role.getRoleCd();
        this.roleNm = role.getRoleNm();
    }
}
