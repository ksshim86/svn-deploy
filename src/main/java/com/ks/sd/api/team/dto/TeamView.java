package com.ks.sd.api.team.dto;

import com.ks.sd.api.team.entity.Team;
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
public class TeamView extends BaseResponse {
    private Integer teamNo;
    private String teamNm;

    @Builder
    public TeamView(Team team) {
        this.teamNo = team.getTeamNo();
        this.teamNm = team.getTeamNm();
        this.setDelYn(team.getDelYn());
        this.setCreatedDt(team.getCreatedDt());
        this.setUpdatedDt(team.getUpdatedDt());
    }
}
