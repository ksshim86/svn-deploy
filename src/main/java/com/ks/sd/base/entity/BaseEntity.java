package com.ks.sd.base.entity;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@MappedSuperclass
@SuperBuilder
@Inheritance
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity {
    @Column(nullable = false)
    @ColumnDefault("'N'")
    @Length(min = 1, max = 1)
    protected String delYn;

    /**
     * 삭제 delYn = "Y"
     */   
    public void delete() {
        this.delYn = "Y";
    }
}
