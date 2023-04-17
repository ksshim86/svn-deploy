package com.ks.sd.api.team.dto;

import com.ks.sd.api.team.entity.Team;
import com.ks.sd.base.dto.BaseTimeResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TeamResponse extends BaseTimeResponse {
    private Integer teamNo;
    private String teamNm;

    @Builder
    public TeamResponse(Team team) {
        this.teamNo = team.getTeamNo();
        this.teamNm = team.getTeamNm();
        this.setCreatedDt(team.getCreatedDt());
        this.setUpdatedDt(team.getUpdatedDt());
    }
}
