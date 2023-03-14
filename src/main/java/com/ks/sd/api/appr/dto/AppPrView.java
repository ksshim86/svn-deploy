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
public class AppPrView {
    private Integer pjtNo;
    private Integer appPrOrdr;
    private String roleCd;
    private String roleNm;
    private String useYn;

    @Builder
    public AppPrView(AppPr appPr) {
        this.pjtNo = appPr.getProject().getPjtNo();
        this.appPrOrdr = appPr.getAppPrOrdr();
        this.roleCd = appPr.getRole().getRoleCd();
        this.roleNm = appPr.getRole().getRoleNm();
        this.useYn = appPr.getUseYn();
    }
}
