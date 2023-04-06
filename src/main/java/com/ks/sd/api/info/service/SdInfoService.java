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

    private final int ONLY_ONE_ROW = 1;

    /**
     * SD 정보 조회
     * @return SdInfo
     */
    public SdInfo getSdInfo() {
        return
            sdInfoRepository.findById(ONLY_ONE_ROW)
                .orElse(SdInfo.builder().build());
    }

    /**
     * SVN DEPLOY ROOT 경로 조회
     * @return String
     */
    public String getSdRootPath() {
        return
            sdInfoRepository.findById(ONLY_ONE_ROW)
                .map(SdInfo::getSdRootPath)
                .orElse("");
    }

    /**
     * SD 정보 저장
     * @param sdInfo
     * @return SdInfo
     */
    public SdInfo saveSdInfo(SdInfo sdInfo) {
        if (sdInfoRepository.findById(ONLY_ONE_ROW).isPresent()) {
            throw new BusinessException(ErrorCode.SD_INFO_ALREADY_EXIST);
        }
        
        Optional.ofNullable(sdInfo.getSdRootPath())
            .filter(sdRootPath -> !sdRootPath.isEmpty())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));


        String sdRootPath = sdInfo.getSdRootPath();

        if (!SdFileUtil.mkdirs(Paths.get(sdRootPath).toString())) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }

        return sdInfoRepository.save(sdInfo);
    }
}
