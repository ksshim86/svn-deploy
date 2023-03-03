package com.ks.sd.api.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.info.entity.SdInfo;

public interface SdInfoRepository extends JpaRepository<SdInfo, Integer> {
    
}
