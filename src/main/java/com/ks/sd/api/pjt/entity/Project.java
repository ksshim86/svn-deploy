package com.ks.sd.api.pjt.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.rev.entity.SdRevision;
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
@Table(indexes = @Index(name = "unique_idex_project_pjtKey", columnList = "pjtKey", unique = true))
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer pjtNo;

    @Column(nullable = false, unique = true)
    private String pjtKey;

    @Column(nullable = false)
    private String pjtNm;

    @Column(nullable = false)
    private String devSvnUrl;

    @Column(nullable = false)
    private String dpSvnUrl;

    @Column(nullable = false)
    private String svnUsername;

    @Column(nullable = false)
    private String svnPassword;

    @Column(nullable = false)
    private Long dcr;

    @Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
    private String startedYn;

    @Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
    private String rcsSt;

    @Column(nullable = false, columnDefinition = "CHAR(1) default 'N'")
    private String dpSt;

    @OneToMany(mappedBy = "project")
    @OrderBy("subPjtNo ASC")
    private List<SubProject> subProjects;

    @OneToMany(mappedBy = "project")
    private List<AppPr> appPrs;

    @OneToMany(mappedBy = "project")
    private List<Deploy> deploys;

    @OneToMany(mappedBy = "project")
    private List<SdRevision> sdRevisions;
    
    // 프로젝트 시작전 수정
    public void beforeStartedUpdate(ProjectUpdateRequest projectUpdateRequest) {
        this.pjtNm = projectUpdateRequest.getPjtNm();
        this.devSvnUrl = projectUpdateRequest.getDevSvnUrl();
        this.dpSvnUrl = projectUpdateRequest.getDpSvnUrl();
        this.svnUsername = projectUpdateRequest.getSvnUsername();
        this.svnPassword = projectUpdateRequest.getSvnPassword();
        this.dcr = projectUpdateRequest.getDcr();
    }

    // 프로젝트 시작시 수정
    public void afterStartedUpdate(ProjectUpdateRequest projectUpdateRequest) {
        this.pjtNm = projectUpdateRequest.getPjtNm();
        this.svnUsername = projectUpdateRequest.getSvnUsername();
        this.svnPassword = projectUpdateRequest.getSvnPassword();
    }

    public void update(ProjectUpdateRequest projectUpdateRequest) {
        this.pjtNm = projectUpdateRequest.getPjtNm();
        this.devSvnUrl = projectUpdateRequest.getDevSvnUrl();
        this.dpSvnUrl = projectUpdateRequest.getDpSvnUrl();
        this.svnUsername = projectUpdateRequest.getSvnUsername();
        this.svnPassword = projectUpdateRequest.getSvnPassword();
        this.dcr = projectUpdateRequest.getDcr();
    }

    public void updateRcsSt(String rcsSt) {
        this.rcsSt = rcsSt;
    }

    public void updateDcr(Long dcr) {
        this.dcr = dcr;
    }
}
