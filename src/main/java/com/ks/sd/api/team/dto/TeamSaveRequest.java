package com.ks.sd.api.team.dto;

import com.ks.sd.api.team.entity.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamSaveRequest {
    private String teamNm;

    public Team toEntity() {
        return Team.builder()
            .teamNm(teamNm).build();
    }
}
