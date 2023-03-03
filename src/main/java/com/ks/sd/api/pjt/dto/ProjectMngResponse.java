package com.ks.sd.api.pjt.dto;

import java.util.List;

import com.ks.sd.api.pjt.entity.Project;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectMngResponse {
    private Integer pjtNo;
    private String pjtKey;
    private String pjtNm;
    private String devSvnUrl;
    private String dpSvnUrl;
    private String svnUsername;
    private String svnPassword;
    private Long dcr;
    private String startedYn;
    private String rcsSt;
    private String dpSt;
    private List<SubProjectMngResponse> subPjtMngResponses;

    @Builder
    public ProjectMngResponse(Project project, List<SubProjectMngResponse> subPjtMngResponses) {
        this.pjtNo = project.getPjtNo();
        this.pjtKey = project.getPjtKey();
        this.pjtNm = project.getPjtNm();
        this.devSvnUrl = project.getDevSvnUrl();
        this.dpSvnUrl = project.getDpSvnUrl();
        this.svnUsername = project.getSvnUsername();
        this.svnPassword = project.getSvnPassword();
        this.dcr = project.getDcr();
        this.startedYn = project.getStartedYn();
        this.rcsSt = project.getRcsSt();
        this.dpSt = project.getDpSt();

        this.subPjtMngResponses = subPjtMngResponses;
    }
}
