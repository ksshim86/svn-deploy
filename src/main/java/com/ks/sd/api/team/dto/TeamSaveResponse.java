package com.ks.sd.api.team.dto;

import com.ks.sd.api.team.entity.Team;
import com.ks.sd.base.dto.BaseResponse;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TeamSaveResponse extends BaseResponse {
    private Integer teamNo;
    private String teamNm;

    @Builder
    public TeamSaveResponse(Team team) {
        this.teamNo = team.getTeamNo();
        this.teamNm = team.getTeamNm();
        this.setDelYn(team.getDelYn());
        this.setCreatedDt(team.getCreatedDt());
        this.setUpdatedDt(team.getUpdatedDt());
    }
}
