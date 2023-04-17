package com.ks.sd.api.role.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleUpdateRequest {
    private String roleCd;
    private String roleNm;
}
