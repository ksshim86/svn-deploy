package com.ks.sd.api.dp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ks.sd.api.dp.entity.Deploy;

public interface DeployRepository extends JpaRepository<Deploy, Integer>, JpaSpecificationExecutor<Deploy> {
}
