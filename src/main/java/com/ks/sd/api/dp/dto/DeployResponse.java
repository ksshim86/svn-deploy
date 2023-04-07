package com.ks.sd.api.dp.dto;

import java.time.LocalDateTime;

import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.base.dto.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DeployResponse extends BaseResponse {
    private Integer dpNo;
    private Integer pjtNo;
    private String dpTitle;
    private String dpDiv;
    private String dpSt;
    private LocalDateTime dpDt;
    private LocalDateTime deadlineDt;

    @Builder
    public DeployResponse(Deploy deploy) {
        this.dpNo = deploy.getDpNo();
        this.pjtNo = deploy.getProject().getPjtNo();
        this.dpTitle = deploy.getDpTitle();
        this.dpDiv = deploy.getDpDiv();
        this.dpSt = deploy.getDpSt();
        this.dpDt = deploy.getDpDt();
        this.deadlineDt = deploy.getDeadlineDt();
        this.setDelYn(deploy.getDelYn());
        this.setCreatedDt(deploy.getCreatedDt());
        this.setUpdatedDt(deploy.getUpdatedDt());
    }
}
