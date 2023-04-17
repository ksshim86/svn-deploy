package com.ks.sd.api.role.dto;

import com.ks.sd.api.role.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RoleResponse {
    private Integer roleLvl;
    private String roleCd;
    private String roleNm;

    @Builder
    public RoleResponse(Role role) {
        this.roleLvl = role.getRoleLvl();
        this.roleCd = role.getRoleCd();
        this.roleNm = role.getRoleNm();
    }
}
