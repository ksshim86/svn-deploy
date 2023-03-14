package com.ks.sd.api.role.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.role.dto.RoleUpdateRequest;
import com.ks.sd.api.user.entity.UserRole;

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
public class Role {
    @Id
    @Column(nullable = false)
    private String roleCd;

    @Column(nullable = false)
    private String roleNm;

    @OneToMany(mappedBy = "role")
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "role")
    private List<AppPr> appPrs;

    public void update(RoleUpdateRequest roleUpdateRequest) {
        this.roleNm = roleUpdateRequest.getRoleNm();
    }
}
