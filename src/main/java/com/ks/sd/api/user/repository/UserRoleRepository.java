package com.ks.sd.api.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.user.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {
    Optional<List<UserRole>> findByUserUserId(String userId);
    Optional<List<UserRole>> findByRoleRoleCd(String roleCd);
}
