package com.ks.sd.api.dp.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.dp.dto.DeployUpdateRequest;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.base.entity.BaseEntity;
import com.ks.sd.consts.SdConstants;

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
public class Deploy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dpNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pjt_no", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String dpTitle;

    // 배포 구분(R: release, H: hotfix)
    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String dpDiv;
    
    // 배포 상태(C: 배포마감, I: 진행중, A: 추가반영, S: stg 반영, SR:stg에서 롤백, M: master 반영)
    @Column(nullable = false, columnDefinition = "CHAR(1) default 'I'")
    private String dpSt;

    @Column(nullable = false)
    private LocalDateTime dpDt;

    @Column(nullable = false)
    private LocalDateTime deadlineDt;

    /**
     * 배포일정을 수정합니다.
     * @param deployUpdateRequest 수정할 배포일정 정보
     */
    public void update(DeployUpdateRequest deployUpdateRequest) {
        this.dpTitle = deployUpdateRequest.getDpTitle();
        this.dpDiv = deployUpdateRequest.getDpDiv();
        this.dpDt = deployUpdateRequest.getDpDt();
        this.deadlineDt = deployUpdateRequest.getDeadlineDt();
    }

    /**
     * 배포 상태를 변경합니다.
     * @param dpSt 배포 상태
     */
    public void updateDpSt(String dpSt) {
        this.dpSt = dpSt;
    }

    /**
     * 배포일정을 삭제합니다.
     */
    public void delete() {
        this.delYn = SdConstants.DELETED;
    }
}
