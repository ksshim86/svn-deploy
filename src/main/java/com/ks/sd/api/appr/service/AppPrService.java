package com.ks.sd.api.appr.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.appr.dto.AppPrUpdateRequest;
import com.ks.sd.api.appr.dto.AppPrView;
import com.ks.sd.api.appr.entity.AppPr;
import com.ks.sd.api.appr.repository.AppPrRepository;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.role.dto.RoleView;
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
    
    public List<AppPrView> getAppPrsByPjtNo(Integer pjtNo) {
        Project project = Project.builder().pjtNo(pjtNo).build();

        List<AppPr> appPrs = 
            appPrRepository.findByProject(project).orElseThrow(() -> new BusinessException(ErrorCode.APPRS_NOT_FOUND));
        List<AppPrView> appPrViews = 
            appPrs.stream()
                .map(appPr -> AppPrView.builder().appPr(appPr).build())
                .collect(Collectors.toList());

        return appPrViews;
    }

    public List<AppPrView> saveAppPrs(Project project) {
        List<RoleView> roleViews = roleService.getAllRoles();

        List<AppPr> appPrs = IntStream.range(0, roleViews.size())
            .mapToObj(i -> {
                RoleView roleView = roleViews.get(i);
                return AppPr.builder()
                    .project(project)
                    .role(Role.builder().roleCd(roleView.getRoleCd()).build())
                    .ordr(i + 1)
                    .build();
            })
            .collect(Collectors.toList());

        List<AppPr> saveAppPrs = appPrRepository.saveAll(appPrs);
        List<AppPrView> appPrViews = 
            saveAppPrs.stream()
                .map(appPr -> AppPrView.builder().appPr(appPr).build())
                .collect(Collectors.toList());

        return appPrViews;
    }
    
    // 승인 절차 사용여부 변경
    public void updateAppPrByPjtNo(AppPrUpdateRequest appPrUpdateRequest) {
        Project project = Project.builder().pjtNo(appPrUpdateRequest.getPjtNo()).build();
        List<AppPr> appPrs = appPrRepository.findByProject(project)
            .orElseThrow(() -> new BusinessException(ErrorCode.APPRS_NOT_FOUND));
        
        appPrs.stream()
            .filter(appPr -> appPr.getOrdr().equals(appPrUpdateRequest.getOrdr()))
            .findFirst()
            .ifPresent(appPr -> {
                appPr.updateUseYn(appPrUpdateRequest.getUseYn());
            });
    }
}
