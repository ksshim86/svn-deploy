package com.ks.sd.api.rev.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.repository.SdPathRepository;

@Transactional
@Service
public class SdPathService {
    @Autowired
    private SdPathRepository sdPathRepository;
    
    public List<SdPath> getSdPathsByPjtNoAndSubPjtNoIsNull(Integer pjtNo) {
        return sdPathRepository.findByIdPjtNoAndSubPjtNoIsNull(pjtNo).get();
    }
}
