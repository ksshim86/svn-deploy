package com.ks.sd.api.rev.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.entity.SdPath.SdPathId;

public interface SdPathRepository extends JpaRepository<SdPath, SdPathId> {
    Optional<List<SdPath>> findByIdPjtNoAndSubPjtNoIsNull(Integer pjtNo);
}
