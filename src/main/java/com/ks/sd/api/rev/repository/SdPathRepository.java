package com.ks.sd.api.rev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.entity.SdPath.SdPathId;

public interface SdPathRepository extends JpaRepository<SdPath, SdPathId> {
    
}
