package com.ks.sd.api.pjt.dto;

import com.ks.sd.api.pjt.entity.SubProject;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubProjectMngResponse {
    private Integer pjtNo;
    private Integer subPjtNo;
    private String subPjtNm;

    @Builder
    public SubProjectMngResponse(SubProject subProject) {
        this.pjtNo = subProject.getProject().getPjtNo();
        this.subPjtNo = subProject.getSubPjtNo();
        this.subPjtNm = subProject.getSubPjtNm();
    }
}
