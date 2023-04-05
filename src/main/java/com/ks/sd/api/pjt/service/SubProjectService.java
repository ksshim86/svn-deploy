package com.ks.sd.api.pjt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.pjt.dto.SubProjectMngResponse;
import com.ks.sd.api.pjt.dto.SubProjectSaveRequest;
import com.ks.sd.api.pjt.dto.SubProjectSaveResponse;
import com.ks.sd.api.pjt.dto.SubProjectUpdateRequest;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.entity.SubProjectId;
import com.ks.sd.api.pjt.repository.SubProjectRepository;
import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.service.SdPathService;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;

@Transactional
@Service
public class SubProjectService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SubProjectService.class);

    @Autowired
    private SubProjectRepository subProjectRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SdPathService sdPathService;

    public List<SubProjectMngResponse> getSubProjects(Integer pjtNo) {
        List<SubProjectMngResponse> subProjectMngResponses = new ArrayList<>();
        
        Optional<List<SubProject>> optSubProjects =
            subProjectRepository.findByProject(
                Project.builder().pjtNo(pjtNo).build()
            );
        
        optSubProjects.ifPresent(subProjects -> {
            subProjects.forEach(subProject -> {
                SubProjectMngResponse subProjectMngResponse
                    = SubProjectMngResponse.builder().subProject(subProject).build();
                    subProjectMngResponses.add(subProjectMngResponse);
            });
        });

        return subProjectMngResponses;
    }

    public SubProjectSaveResponse save(SubProjectSaveRequest subProjectSaveRequest) {
        final String STARTED = "Y";
        final String COMPLETION = "N";

        Integer pjtNo = subProjectSaveRequest.getPjtNo();

        // 서브 프로젝트 개수 조회
        Integer subPjtCnt = subProjectRepository.countByProject(subProjectSaveRequest.toEntity().getProject());

        // 프로젝트 조회
        Project project = projectService.getProjectByPjtNo(pjtNo);
        
        // 서브 프로젝트 이름 중복 확인
        Optional<List<SubProject>> optSubProjects = subProjectRepository.findByProject(project);

        optSubProjects.ifPresent(subProjects -> {
            subProjects.forEach(subProject -> {
                if (subProject.getSubPjtNm().equals(subProjectSaveRequest.getSubPjtNm())) {
                    throw new BusinessException(ErrorCode.SUB_PJT_NM_DUPLICATION);
                }
            });
        });

        // 서브 프로젝트 저장
        SubProject subProject = subProjectRepository.save(subProjectSaveRequest.toEntity(subPjtCnt));

        // 프로젝트 시작 여부 확인
        if (STARTED.equals(project.getStartedYn())) {
            // 리비전 수집 상태(rcs_st)와 배포 상태(dp_st)가 N일때 까지 대기
            while ("Y".equals(project.getRcsSt()) || "Y".equals(project.getDpSt())) {
                try {
                    LOGGER.info("Waiting for rcs_st and dp_st to be N: rcsSt={}, dpSt={}", project.getRcsSt(), project.getDpSt());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new BusinessException(ErrorCode.PJT_ST_STARTED);
                }
    
                project = projectService.getProjectByPjtNo(pjtNo);
            }

            // 리비전 수집 시작 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, STARTED);

            // 서브 프로젝트 번호가 없는 파일 조회
            List<SdPath> sdPaths = sdPathService.getSdPathsByPjtNoAndSubPjtNoIsNull(pjtNo);

            sdPaths.forEach(sdPath -> {
                // 서브 프로젝트 조건 확인
                int subPjtNo = checkSubProjectConditions(subProject, sdPath.getFilePath(), sdPath.getFileNm());

                // 서브 프로젝트에 해당하는 파일이면 서브 프로젝트 번호 업데이트
                if (subPjtNo != -1) {
                    LOGGER.info(
                        "Updated subPjtNo({}): pjtNo={}, revNo={}, action={}, filePath={}, fileNm={}",
                        subPjtNo, pjtNo, sdPath.getId().getRevNo(), sdPath.getAction(), sdPath.getFilePath(), sdPath.getFileNm()
                    );
                    sdPath.updateSubPjtNo(subPjtNo);
                }
            });

            // 리비전 수집 완료 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, COMPLETION);
        }

        return SubProjectSaveResponse.builder().subProject(subProject).build();
    }

    // 서브 프로젝트 조건 확인
    private int checkSubProjectConditions(SubProject subProject, String filePath, String fileNm) {
        String subPjtNm = subProject.getSubPjtNm();

        boolean condition1 = subPjtNm.equals(fileNm);
        boolean condition2 = filePath.startsWith("/") && filePath.substring(1).startsWith(subPjtNm + "/");

        if (condition1 || condition2) {
            return subProject.getSubPjtNo();
        }

        return -1;
    }

    public SubProjectSaveResponse update(SubProjectUpdateRequest subProjectUpdateRequest) {
        SubProjectId subProjectId = 
            SubProjectId.builder()
            .project(subProjectUpdateRequest.getPjtNo())
            .subPjtNo(subProjectUpdateRequest.getSubPjtNo()).build();

        SubProject subProject = subProjectRepository.findById(subProjectId)
            .orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));
            
        return subProject.update(subProjectUpdateRequest.toEntity());
    }
}
