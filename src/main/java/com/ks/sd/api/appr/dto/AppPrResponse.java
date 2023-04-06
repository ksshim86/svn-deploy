package com.ks.sd.api.appr.dto;

import com.ks.sd.api.appr.entity.AppPr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class AppPrResponse {
    private Integer pjtNo;
    private Integer ordr;
    private String roleCd;
    private String roleNm;
    private String useYn;

    @Builder
    public AppPrResponse(AppPr appPr) {
        this.pjtNo = appPr.getProject().getPjtNo();
        this.ordr = appPr.getOrdr();
        this.roleCd = appPr.getRole().getRoleCd();
        this.roleNm = appPr.getRole().getRoleNm();
        this.useYn = appPr.getUseYn();
    }
}
