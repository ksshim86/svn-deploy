package com.ks.sd.api.rev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.rev.dto.SdRevisionSaveRequest;
import com.ks.sd.api.rev.entity.SdRevision;
import com.ks.sd.api.rev.repository.SdRevisionRepository;

@Transactional
@Service
public class SdRevisionService {
    @Autowired
    private SdRevisionRepository sdRevisionRepository;

    public SdRevision save(SdRevisionSaveRequest sdRevisionSaveRequest) {
        return sdRevisionRepository.save(sdRevisionSaveRequest.toEntity());
    }
}
