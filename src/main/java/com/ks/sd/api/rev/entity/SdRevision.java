package com.ks.sd.api.rev.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.pjt.entity.Project;

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
public class SdRevision implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private SdRevisionId id;

    @MapsId("pjtNo")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "pjt_no")
    private Project project;

    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false)
    private LocalDateTime revDt;

    @Column(columnDefinition = "TEXT")
    private String msg;

    @OneToMany(mappedBy = "sdRevision")
    private List<SdPath> sdPaths;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SdRevisionId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer pjtNo;
        @Column(name = "rev_no", nullable = false)
        private Integer revNo;
    }
}
