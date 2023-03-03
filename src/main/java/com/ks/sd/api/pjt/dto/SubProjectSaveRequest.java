package com.ks.sd.api.pjt.dto;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SubProjectSaveRequest {
    private Integer pjtNo;
    private String subPjtNm;

    public SubProject toEntity() {
        return SubProject.builder()
            .subPjtNm(subPjtNm)
            .project(Project.builder().pjtNo(pjtNo).build()).build();
    }

    public SubProject toEntity(Integer subPjtCnt) {
        return SubProject.builder()
            .subPjtNo(subPjtCnt + 1)
            .subPjtNm(subPjtNm)
            .project(Project.builder().pjtNo(pjtNo).build()).build();
    }
}
