package com.ks.sd.api.rev.entity;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.ks.sd.api.pjt.entity.SubProject;

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
public class SdPath implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @EmbeddedId
    private SdPathId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "pjt_no", referencedColumnName = "pjt_no", insertable = false, updatable = false),
        @JoinColumn(name = "rev_no", referencedColumnName = "rev_no", insertable = false, updatable = false)
    })
    private SdRevision sdRevision;

    @Column(name = "sub_pjt_no")
    private Integer subPjtNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "pjt_no", referencedColumnName = "pjt_no", insertable = false, updatable = false),
        @JoinColumn(name = "sub_pjt_no", referencedColumnName = "sub_pjt_no", insertable = false, updatable = false)
    })
    private SubProject subProject;

    @Column(nullable = false)
    private String combined;

    @Column(nullable = false, columnDefinition = "CHAR(1)")
    private String action;

    @Column(nullable = false, columnDefinition = "CHAR(1)")
	private String kind;

    @Column(nullable = false, columnDefinition = "TEXT")
	private String filePath;

    @Column(nullable = false, columnDefinition = "VARCHAR(1000)")
	private String fileNm;

	private Integer copyRevNo;

	private String copyFilePath;

	private String copyFileNm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class SdPathId implements Serializable {
        private static final long serialVersionUID = 1L;
        @Column(name = "pjt_no", nullable = false)
        private Integer pjtNo;
        @Column(name = "rev_no", nullable = false)
        private Integer revNo;
        @Column(name = "ordr", nullable = false)
        private Integer ordr;
    }

    @PrePersist
    @PreUpdate
    private void updateCombinedKeyHash() {
        String combinedKey = String.format("%d|%s|%s", this.id.getPjtNo(), this.filePath, this.fileNm);
        combined = sha256(combinedKey);
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }

    public void updateSubPjtNo(Integer subPjtNo) {
        this.subPjtNo = subPjtNo;
    }
}
