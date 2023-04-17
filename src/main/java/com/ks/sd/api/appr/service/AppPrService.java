package com.ks.sd.api.appr.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.appr.dto.AppPrUpdateRequest;
import com.ks.sd.api.appr.dto.AppPrResponse;
import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.appr.entity.AppPr.AppPrId;
import com.ks.sd.api.appr.repository.AppPrRepository;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.role.dto.RoleResponse;
import com.ks.sd.api.role.entity.Role;
import com.ks.sd.api.role.service.RoleService;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class AppPrService {
    @Autowired
    private AppPrRepository appPrRepository;

    @Autowired
    private RoleService roleService;
    
    /**
     * 주어진 프로젝트 번호에 해당하는 승인 절차를 반환합니다.
     * @param pjtNo 승인 절차를 반환할 프로젝트 번호입니다.
     * @return 승인 절차를 나타내는 AppPrResponse 인스턴스의 목록을 반환합니다.
     */
    public List<AppPrResponse> getAppPrsByPjtNo(Integer pjtNo) {
        Project project = Project.builder().pjtNo(pjtNo).build();

        List<AppPr> appPrs = appPrRepository.findByProject(project)
            .orElseThrow(() -> new BusinessException(ErrorCode.APPRS_NOT_FOUND));

        List<AppPrResponse> responses = appPrs.stream()
                .map(appPr -> AppPrResponse.builder().appPr(appPr).build())
                .collect(Collectors.toList());

        return responses;
    }

    /**
     * 주어진 프로젝트와 관련된 승인 절차를 저장합니다.
     * @param project 승인 절차가 저장되는 프로젝트입니다.
     * @return 저장된 승인 절차를 나타내는 AppPrResponse 인스턴스의 목록을 반환합니다.
     */
    public List<AppPrResponse> saveAppPrs(Project project) {
        List<RoleResponse> roleResponses = roleService.getAllRoles();

        List<AppPr> appPrs = 
            IntStream.range(0, roleResponses.size())
                .mapToObj(i -> {
                    RoleResponse roleResponse = roleResponses.get(i);
                    return AppPr.builder()
                        .project(project)
                        .role(Role.builder().roleCd(roleResponse.getRoleCd()).build())
                        .ordr(i + 1)
                        .build();
                })
                .collect(Collectors.toList());

        List<AppPr> saveAppPrs = appPrRepository.saveAll(appPrs);

        List<AppPrResponse> responses = 
            saveAppPrs.stream()
                .map(appPr -> AppPrResponse.builder().appPr(appPr).build())
                .collect(Collectors.toList());

        return responses;
    }
    
    /**
     * 주어진 프로젝트 번호와 순서에 해당하는 승인 절차의 사용 여부를 수정합니다.
     * @param appPrUpdateRequest 승인 절차의 사용 여부를 수정하는 요청입니다.
     * @return 수정된 승인 절차를 나타내는 AppPrResponse 인스턴스를 반환합니다.
     */
    public AppPrResponse updateUseYnByPjtNoAndOrdr(AppPrUpdateRequest appPrUpdateRequest) {
        AppPrId appPrId = 
            AppPrId.builder()
                .project(appPrUpdateRequest.getPjtNo())
                .ordr(appPrUpdateRequest.getOrdr())
                .build();

        AppPr appPr = appPrRepository.findById(appPrId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPRS_NOT_FOUND));

        appPr.updateUseYn(appPrUpdateRequest.getUseYn());

        return AppPrResponse.builder().appPr(appPr).build();
    }
}
