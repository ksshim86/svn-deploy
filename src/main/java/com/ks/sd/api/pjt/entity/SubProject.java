package com.ks.sd.api.pjt.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.base.entity.BaseTimeEntity;

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
@Table(indexes = @Index(name = "unique_idex_sub_project_subPjtNm", columnList = "subPjtNm", unique = true))
public class SubProject extends BaseTimeEntity {
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
}
