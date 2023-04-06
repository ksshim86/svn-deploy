package com.ks.sd.api.appr.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.base.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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
@IdClass(AppPr.AppPrId.class)
public class AppPr extends BaseEntity {
    private static final long serialVersionUID = 1L;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "pjt_no")
    private Project project;

    @Id
    @Column(nullable = false)
    private Integer ordr;

    @ManyToOne
    @JoinColumn(name = "role_cd", nullable = false)
    private Role role;

    @ColumnDefault("'N'")
    @Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
    private String useYn;

    /**
     * 승인 절차 사용 여부 수정
     * @param useYn 승인 절차 사용 여부
     */
    public void updateUseYn(String useYn) {
        this.useYn = useYn;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppPrId implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private Integer project;
        private Integer ordr;
    }
}
