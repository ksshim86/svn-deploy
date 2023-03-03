package com.ks.sd.api.pjt.dto;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.base.dto.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProjectSaveRequest extends BaseRequest {
    private String pjtKey;
    private String pjtNm;
    private String devSvnUrl;
    private String dpSvnUrl;
    private String svnUsername;
    private String svnPassword;
    private Long dcr;

    public Project toEntity() {
        return Project.builder()
            .pjtKey(pjtKey)
            .pjtNm(pjtNm)
            .devSvnUrl(devSvnUrl)
            .dpSvnUrl(dpSvnUrl)
            .svnUsername(svnUsername)
            .svnPassword(svnPassword)
            .dcr(dcr).build();
    }
}
