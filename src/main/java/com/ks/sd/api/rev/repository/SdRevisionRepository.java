package com.ks.sd.api.rev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ks.sd.api.rev.entity.SdRevision;
import com.ks.sd.api.rev.entity.SdRevision.SdRevisionId;

public interface SdRevisionRepository extends JpaRepository<SdRevision, SdRevisionId> {
    
}
