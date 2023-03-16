package com.ks.sd.api.appr.dto;

import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.pjt.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppPrUpdateRequest {
    private Integer pjtNo;
    private Integer ordr;
    private String useYn;

    public AppPr toEntity() {
        return AppPr.builder()
            .project(Project.builder().pjtNo(pjtNo).build())
            .ordr(ordr)
            .useYn(useYn).build();
    }
}
