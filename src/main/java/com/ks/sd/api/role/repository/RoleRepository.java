package com.ks.sd.api.role.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.role.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<List<Role>> findAllByOrderByRoleLvlAsc(); 
    Optional<Role> findByRoleCd(String roleCd);
}
