package com.ks.sd.api.dp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.dp.dto.DeploySaveRequest;
import com.ks.sd.api.dp.dto.DeployUpdateRequest;
import com.ks.sd.api.dp.dto.DeployView;
import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.api.dp.repository.DeployRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class DeployService {
    @Autowired
    private DeployRepository deployRepository;

    /**
     * 삭제여부로 배포 목록 조회
     * @param delYn
     * @return
     */
    public List<DeployView> getDeploysByDelYn(String delYn) {
        List<Deploy> deploys = deployRepository.findByDelYnOrderByDpDtDescProjectAsc(delYn)
            .orElseThrow(() -> new BusinessException(ErrorCode.DEPLOY_NOT_FOUND));

        List<DeployView> deployViews = deploys.stream()
            .map(deploy -> DeployView.builder().deploy(deploy).build())
            .collect(Collectors.toList());
        
        return deployViews;
    }

    public Deploy getDeployByDpNo(Integer dpNo) {
        return deployRepository.findById(dpNo).orElseThrow(() -> new BusinessException(ErrorCode.DEPLOY_NOT_FOUND));
    }

    public Deploy saveDeploy(DeploySaveRequest deploySaveRequest) {
        return deployRepository.save(deploySaveRequest.toEntity());
    }

    public Deploy updatDeploy(Integer dpNo, DeployUpdateRequest deployUpdateRequest) {
        Deploy deploy = this.getDeployByDpNo(dpNo);
        deploy.update(deployUpdateRequest);

        return deploy;
    }

    public void deleteDeploy(Integer dpNo) {
        Deploy deploy = this.getDeployByDpNo(dpNo);
        deploy.delete();
    }
}
