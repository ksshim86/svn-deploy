package com.ks.sd.api.dp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.dp.dto.DeploySaveRequest;
import com.ks.sd.api.dp.dto.DeployUpdateRequest;
import com.ks.sd.api.dp.dto.DeployResponse;
import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.api.dp.repository.DeployRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;
import com.ks.sd.util.sort.SortCriteriaUtil;

@Transactional
@Service
public class DeployService {
    @Autowired
    private DeployRepository deployRepository;
    
    /**
     * 배포 목록을 검색합니다.
     * @param pjtNo 프로젝트 번호 (선택 사항)
     * @param dpDiv 배포 구분 (선택 사항)
     * @param delYn 삭제 여부 (선택 사항)
     * @param sort 정렬 기준 목록 (선택 사항)
     * @return 검색된 배포 목록에 대한 {@link DeployResponse} 리스트
     */
    public List<DeployResponse> searchDeploys(Integer pjtNo, String dpDiv, String delYn, String sort) {
        Sort sortObj = Sort.unsorted();
        Specification<Deploy> spec = Specification.where(null);

        if (pjtNo != null) {
            spec = spec.and((root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("project").get("pjtNo"), pjtNo));
        }

        if (dpDiv != null && !dpDiv.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dpDiv"), dpDiv));
        }

        if (delYn != null && !delYn.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("delYn"), delYn));
        }

        if (sort != null && !sort.isEmpty()) {
            sortObj = SortCriteriaUtil.processSort(sort);
        }

        List<Deploy> deploys = deployRepository.findAll(spec, sortObj);

        return 
            deploys.stream()
                .map(deploy -> DeployResponse.builder().deploy(deploy).build())
                .collect(Collectors.toList());
    }

    /**
     * 배포일정을 저장합니다.
     * @param deploySaveRequest 저장할 배포일정 정보
     * @return 저장된 배포일정에 대한 {@link DeployResponse}
     */
    public DeployResponse saveDeploy(DeploySaveRequest deploySaveRequest) {
        Deploy deploy = deployRepository.save(deploySaveRequest.toEntity());

        return DeployResponse.builder().deploy(deploy).build();
    }

    /**
     * 배포일정을 수정합니다.
     * @param deployUpdateRequest 수정할 배포일정 정보
     * @return 수정된 배포일정에 대한 {@link DeployResponse}
     */
    public DeployResponse updatDeploy(DeployUpdateRequest deployUpdateRequest) {
        Deploy deploy = this.getDeployByDpNo(deployUpdateRequest.getDpNo());
        deploy.update(deployUpdateRequest);

        return DeployResponse.builder().deploy(deploy).build();
    }

    /**
     * 배포일정을 삭제합니다.
     * @param dpNo 삭제할 배포일정 번호
     */
    public void deleteDeploy(Integer dpNo) {
        Deploy deploy = this.getDeployByDpNo(dpNo);
        deploy.delete();
    }

    /**
     * 배포일정 번호로 배포일정을 조회합니다.
     * @param dpNo 배포일정 번호
     * @return 조회된 배포일정에 대한 {@link Deploy}
     */
    public Deploy getDeployByDpNo(Integer dpNo) {
        return deployRepository.findById(dpNo).orElseThrow(() -> new BusinessException(ErrorCode.DEPLOY_NOT_FOUND));
    }
}
