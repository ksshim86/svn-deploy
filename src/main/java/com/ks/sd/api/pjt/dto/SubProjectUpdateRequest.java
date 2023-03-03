package com.ks.sd.api.pjt.dto;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SubProjectUpdateRequest {
    private Integer pjtNo;
    private Integer subPjtNo;
    private String subPjtNm;

    public SubProject toEntity() {
        return SubProject.builder()
            .subPjtNo(subPjtNo)
            .subPjtNm(subPjtNm)
            .project(Project.builder().pjtNo(pjtNo).build())
            .build();
    }
}
