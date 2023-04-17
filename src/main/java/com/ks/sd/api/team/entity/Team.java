package com.ks.sd.api.team.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.team.dto.TeamUpdateRequest;
import com.ks.sd.api.user.entity.User;
import com.ks.sd.base.entity.BaseEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@ToString(callSuper = true)
@SuperBuilder
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Team extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer teamNo;

    @Column(nullable = false)
    private String teamNm;

    @OneToMany(mappedBy = "team")
    private List<User> users;

    /**
     * 팀을 수정합니다.
     * @param teamUpdateRequest 팀 수정 요청
     */
    public void update(TeamUpdateRequest teamUpdateRequest) {
        this.teamNm = teamUpdateRequest.getTeamNm();
    }
}
