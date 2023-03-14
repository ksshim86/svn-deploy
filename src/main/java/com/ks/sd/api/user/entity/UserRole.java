package com.ks.sd.api.user.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ks.sd.api.role.entity.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@IdClass(UserRoleId.class)
@Getter
@Entity
@NoArgsConstructor
public class UserRole implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userRoleNo;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "role_cd")
    private Role role;

    @Builder
    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }
}
