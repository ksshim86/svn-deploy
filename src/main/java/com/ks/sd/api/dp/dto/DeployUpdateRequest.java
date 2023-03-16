package com.ks.sd.api.dp.dto;

import java.time.LocalDateTime;

import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.api.pjt.entity.Project;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeployUpdateRequest {
    private Integer pjtNo;
    private String dpTitle;
    private String dpDiv;
    private LocalDateTime dpDt;
    private LocalDateTime deadlineDt;

    public Deploy toEntity() {
        return Deploy.builder()
            .project(Project.builder().pjtNo(pjtNo).build())
            .dpTitle(dpTitle)
            .dpDiv(dpDiv)
            .dpDt(dpDt)
            .deadlineDt(deadlineDt).build();
    }
}
