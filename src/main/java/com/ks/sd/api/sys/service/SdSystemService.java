package com.ks.sd.api.sys.service;

import java.nio.file.Paths;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks.sd.api.sys.entity.SdSystem;
import com.ks.sd.api.sys.repository.SdSystemRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;
import com.ks.sd.util.file.SdFileUtil;

@Transactional
@Service
public class SdSystemService {
    @Autowired
    private SdSystemRepository sdSystemRepository;

    // 시스템 정보 조회
    public SdSystem getSdSystem() {
        Optional<SdSystem> optSdSystem = sdSystemRepository.findById(1);
        
        if (optSdSystem.isPresent()) {
            return optSdSystem.get();
        } else {
            return SdSystem.builder().build();
        }
    }

    public SdSystem saveSdSystem(SdSystem sdSystem) {
        String dpPath = sdSystem.getDpPath();

        if (sdSystem.getDpPath() == null || sdSystem.getDpPath().equals("")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!SdFileUtil.mkdirs(Paths.get(dpPath).toString())) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }

        return sdSystemRepository.save(sdSystem);
    }
}
