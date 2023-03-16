package com.ks.sd.api.pjt.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.pjt.dto.SubProjectSaveResponse;
import com.ks.sd.api.rev.entity.SdPath;
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
@IdClass(SubProjectId.class)
public class SubProject extends BaseEntity {
    @Id
    @Column(name = "sub_pjt_no", nullable = false)
    private Integer subPjtNo;
    
    @Column(nullable = false)
    private String subPjtNm;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pjt_no")
    private Project project;

    @OneToMany(mappedBy = "subProject")
    private List<SdPath> sdPaths;

    public SubProjectSaveResponse update(SubProject subProject) {
        this.subPjtNm = subProject.getSubPjtNm();

        return SubProjectSaveResponse.builder()
            .subProject(this)
            .build();
    }
}
