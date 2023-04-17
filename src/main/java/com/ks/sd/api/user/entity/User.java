package com.ks.sd.api.user.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.team.entity.Team;
import com.ks.sd.api.user.dto.UserUpdateRequest;
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
public class User extends BaseEntity {
    @Id
    private String userId;
    private String userNm;
    private String password;

    @ManyToOne
    @JoinColumn(name = "team_no")
    private Team team;

    @OneToMany(mappedBy = "user")
    private List<UserRole> userRoles;

    /**
     * 사용자를 수정합니다.
     * @param userUpdateRequest 사용자 수정 요청
     */
    public void update(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest.getUserNm() != null) {
            this.userNm = userUpdateRequest.getUserNm();
        }
        if (userUpdateRequest.getPassword() != null) {
            this.password = userUpdateRequest.getPassword();
        }
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
