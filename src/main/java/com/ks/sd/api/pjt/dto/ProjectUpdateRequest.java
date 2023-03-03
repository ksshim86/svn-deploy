package com.ks.sd.api.pjt.dto;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.base.dto.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProjectUpdateRequest extends BaseRequest {
    private Integer pjtNo;
    private String pjtNm;
    private String devSvnUrl;
    private String dpSvnUrl;
    private String svnUsername;
    private String svnPassword;
    private Long dcr;
    private String startedYn;
    private String rcsSt;
    private String dpSt;

    public Project toEntity() {
        return Project.builder()
            .pjtNo(pjtNo)
            .pjtNm(pjtNm)
            .devSvnUrl(devSvnUrl)
            .dpSvnUrl(dpSvnUrl)
            .svnUsername(svnUsername)
            .svnPassword(svnPassword)
            .delYn(getDelYn())
            .dcr(dcr)
            .startedYn(startedYn)
            .rcsSt(rcsSt)
            .dpSt(dpSt).build();
    }
}
