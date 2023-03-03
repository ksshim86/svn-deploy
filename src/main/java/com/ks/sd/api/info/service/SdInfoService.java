package com.ks.sd.api.info.service;

import java.nio.file.Paths;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks.sd.api.info.entity.SdInfo;
import com.ks.sd.api.info.repository.SdInfoRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;
import com.ks.sd.util.file.SdFileUtil;

@Transactional
@Service
public class SdInfoService {
    @Autowired
    private SdInfoRepository sdInfoRepository;

    // 시스템 정보 조회
    public SdInfo getSdSystem() {
        Optional<SdInfo> optSdInfo = sdInfoRepository.findById(1);
        
        if (optSdInfo.isPresent()) {
            return optSdInfo.get();
        } else {
            return SdInfo.builder().build();
        }
    }

    public SdInfo saveSdSystem(SdInfo sdInfo) {
        String dpPath = sdInfo.getDpPath();

        if (sdInfo.getDpPath() == null || sdInfo.getDpPath().equals("")) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (!SdFileUtil.mkdirs(Paths.get(dpPath).toString())) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }

        return sdInfoRepository.save(sdInfo);
    }
}
