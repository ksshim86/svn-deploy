package com.ks.sd.api.sys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.sys.entity.SdSystem;

public interface SdSystemRepository extends JpaRepository<SdSystem, Integer> {
    
}
