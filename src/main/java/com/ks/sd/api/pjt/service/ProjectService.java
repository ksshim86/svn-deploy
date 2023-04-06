package com.ks.sd.api.pjt.service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.appr.repository.AppPrRepository;
import com.ks.sd.api.info.service.SdInfoService;
import com.ks.sd.api.pjt.dto.ProjectResponse;
import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.pjt.dto.ProjectWithSubProjectsResponse;
import com.ks.sd.api.pjt.dto.SubProjectResponse;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.repository.ProjectRepository;
import com.ks.sd.api.pjt.repository.SubProjectRepository;
import com.ks.sd.consts.SdConstants;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;
import com.ks.sd.util.file.SdFileUtil;
import com.ks.sd.util.svn.SvnRepositoryUtil;

@Service
public class ProjectService {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private SdInfoService sdInfoService;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SubProjectRepository subProjectRepository;

    @Autowired
    private AppPrRepository appPrRepository;

    /**
     * 프로젝트 검색
     * @param delYn
     * @return
     */
    @Transactional
    public List<ProjectResponse> searchProjects(String delYn) {
        Specification<Project> spec = (root, query, criteriaBuilder) -> null;

        if (delYn != null && !delYn.isEmpty()) {
            spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("delYn"), delYn));
        }

        List<Project> projects = projectRepository.findAll(spec);

        List<ProjectResponse> responses = 
            projects.stream()
            .map(project -> ProjectResponse.builder().project(project).build())
            .collect(Collectors.toList());
        
        return responses;
    }

    /**
     * 프로젝트 목록(서브 프로젝트 포함) 조회
     * @return
     */
    @Transactional
    public List<ProjectWithSubProjectsResponse> getProjectsWithSubProjects() {
        // 삭제되지 않은 프로젝트 목록 조회
        List<Project> projects = 
            projectRepository.findByDelYn(SdConstants.UNDELETED)
                .orElseThrow(() -> new BusinessException(ErrorCode.PJT_NOT_FOUND));

        return 
            projects.stream().map(project -> {
                // 서브 프로젝트 목록 조회
                List<SubProjectResponse> subProjectResponses = 
                    project.getSubProjects().stream()
                    .map(subProject -> SubProjectResponse.builder().subProject(subProject).build())
                    .collect(Collectors.toList());
                
                // 프로젝트 및 서브 프로젝트를 응답 객체로 변환
                return ProjectWithSubProjectsResponse.builder()
                    .project(project)
                    .subProjectResponses(subProjectResponses)
                    .build();
            }).collect(Collectors.toList());
    }

    /**
     * 프로젝트 조회
     * @param pjtNo
     * @return
     */
    public Project getProjectByPjtNo(Integer pjtNo) {
        Project project = 
            projectRepository.findById(pjtNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.PJT_NOT_FOUND));

        return project;
    }

    /**
     * 프로젝트 목록 조회
     * @param delYn 삭제여부
     * @param dpSt 배포상태
     * @param startedYn 시작여부
     * @param rcsSt 리비전 수집 상태
     * @return
     */
    @Transactional
    public List<ProjectResponse> getProjectsByDelYnAndDpStAndStartedYnAndRcsSt(
        String delYn, String dpSt, String startedYn, String rcsSt
    ) {
        List<ProjectResponse> projectResponses = new ArrayList<ProjectResponse>();

        Optional<List<Project>> optProjects = 
            projectRepository.findByDelYnAndDpStAndStartedYnAndRcsSt(delYn, dpSt, startedYn, rcsSt);

        optProjects.ifPresent(projects -> {
            projects.forEach(project -> {
                projectResponses.add(ProjectResponse.builder().project(project).build());
            });
        });

        return projectResponses;
    }
    
    /**
     * 프로젝트 저장
     * @param projectSaveRequest
     * @return
     * @throws Exception
     */
    @Transactional
    public ProjectResponse saveProject(ProjectSaveRequest projectSaveRequest) throws Exception {
        Project project = new Project();
        String devSvnUrl = projectSaveRequest.getDevSvnUrl();
        String dpSvnUrl = projectSaveRequest.getDpSvnUrl();
        String svnUsername = projectSaveRequest.getSvnUsername();
        String svnPassword = projectSaveRequest.getSvnPassword();
        String pjtKey = projectSaveRequest.getPjtKey();

        validateSvnConnections(devSvnUrl, dpSvnUrl, svnUsername, svnPassword);

        String sdRootPath = sdInfoService.getSdRootPath();
        String pjtDirPath = Paths.get(sdRootPath, pjtKey).toString();

        try {
            // 프로젝트 디렉토리 생성
            createProjectDirectory(pjtDirPath);
            // 배포 SVN 체크아웃
            checkoutDpSvnRepository(dpSvnUrl, svnUsername, svnPassword, pjtDirPath);
            // 배포 SVN 브랜치 생성
            createDpSvnBranches(dpSvnUrl, svnUsername, svnPassword);
            // 배포 SVN 업데이트
            updateDpSvnRepository(dpSvnUrl, svnUsername, svnPassword, pjtDirPath);
        } catch (BusinessException e) {
            LOGGER.info("오류 발생, 생성된 폴더 삭제");
            SdFileUtil.deleteDirectory(pjtDirPath);
            throw e;
        }

        projectSaveRequest.setDcr(0L);
        project = projectRepository.save(projectSaveRequest.toEntity());

        return ProjectResponse.builder().project(project).build();
    }

    /**
     * 프로젝트 수정
     */
    @Transactional
    public ProjectResponse updateProject(ProjectUpdateRequest projectUpdateRequest) {
        Project project = 
            projectRepository.findById(projectUpdateRequest.getPjtNo())
                .orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));

        // 프로젝트 SVN 설정 변경
        updateSvnSettings(projectUpdateRequest, project);

        // 프로젝트 시작 여부
        if (SdConstants.STARTED.equals(project.getStartedYn())) {
            project.afterStartedUpdate(projectUpdateRequest);
        } else {
            project.beforeStartedUpdate(projectUpdateRequest);
        }

        return ProjectResponse.builder().project(project).build();
    }

    /**
     * 프로젝트 삭제
     * @param pjtNo
     */
    @Transactional
    public void deleteProject(Integer pjtNo) {
        Project project = projectRepository.findById(pjtNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.DELETE_TARGET_NOT_FOUND));

        LOGGER.info("프로젝트 시작 여부 확인");
        if (project.getStartedYn().equals("N")) {
            LOGGER.info("프로젝트 시작 X");
            LOGGER.info("프로젝트 디렉토리 삭제");
            String sdRootPath = sdInfoService.getSdRootPath();
            SdFileUtil.deleteDirectory(Paths.get(sdRootPath, project.getPjtKey()).toString());
            
            LOGGER.info("승인 절차 완전 삭제");
            appPrRepository.deleteByProject(project);
            LOGGER.info("프로젝트 정보 완전 삭제");
            subProjectRepository.deleteByProject(project);
            projectRepository.deleteByPjtNo(project.getPjtNo());
        } else {
            LOGGER.info("프로젝트 시작 O");
            LOGGER.info("프로젝트 정보 삭제");
            project.delete();
        }
    }

    /**
     * 프로젝트 리비전 수집 시작 상태 수정
     * @param pjtNo
     * @param rcsSt
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProjectByRcsSt(Integer pjtNo, String rcsSt) {
        Project project = projectRepository.findById(pjtNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));

        project.updateRcsSt(rcsSt);
    }

    /**
     * 프로젝트 디렉토리 생성
     * @param pjtDirPath
     */
    private void createProjectDirectory(String pjtDirPath) {
        LOGGER.info("프로젝트 디렉토리 생성: {}", pjtDirPath);

        if (!SdFileUtil.mkdirs(pjtDirPath)) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }
    }

    /**
     * 배포 SVN 체크아웃
     * @param devSvnUrl
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     */
    private void checkoutDpSvnRepository(String dpSvnUrl, String svnUsername, String svnPassword, String pjtDirPath) {
        LOGGER.info("프로젝트 디렉토리에 배포 SVN 체크아웃");

        if (!SvnRepositoryUtil.checkout(dpSvnUrl, svnUsername, svnPassword, pjtDirPath)) {
            throw new BusinessException(ErrorCode.SVN_REPO_CHECKOUT_FAILED);
        }
    }

    /**
     * 배포 SVN에 trunk, staging, master 브랜치 생성
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     */
    private void createDpSvnBranches(String dpSvnUrl, String svnUsername, String svnPassword) {
        LOGGER.info("배포 SVN에 trunk, staging, master 브랜치 생성");
        
        String[][] branchInfo = {
            {"", "/trunk", "Creating trunk branch"},
            {"/trunk", "/branches/staging", "Creating staging branch"},
            {"/branches/staging", "/branches/master", "Creating master branch"}
        };

        for (String[] branch : branchInfo) {
            String orgBranchNm = branch[0];
            String dstBranchNm = branch[1];
            String message = branch[2];

            LOGGER.info(message);
            if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                throw new BusinessException(ErrorCode.SVN_BRANCH_CREATE_FAILED);
            }
        }
    }

    /**
     * 프로젝트 폴더의 배포 SVN 업데이트
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     * @param pjtDirPath
     */
    private void updateDpSvnRepository(String dpSvnUrl, String svnUsername, String svnPassword, String pjtDirPath) {
        LOGGER.info("배포 SVN 업데이트");

        long headRevision = -1;
        if (!SvnRepositoryUtil.update(dpSvnUrl, svnUsername, svnPassword, pjtDirPath, headRevision)) {
            throw new BusinessException(ErrorCode.SVN_UPDATE_FAILED);
        }
    }

    /**
     * 프로젝트 SVN 설정 변경
     * @param projectUpdateRequest
     * @param project
     */
    private void updateSvnSettings(ProjectUpdateRequest projectUpdateRequest, Project project) {
        String devSvnUrl = projectUpdateRequest.getDevSvnUrl();
        String dpSvnUrl = projectUpdateRequest.getDpSvnUrl();
        String svnUsername = projectUpdateRequest.getSvnUsername();
        String svnPassword = projectUpdateRequest.getSvnPassword();
        
        // 프로젝트 시작 여부
        if (SdConstants.STARTED.equals(project.getStartedYn())) {
            // svnUsername, svnPassword 변경 여부 확인
            if (!project.getSvnUsername().equals(svnUsername) || !project.getSvnPassword().equals(svnPassword)) {
                // 개발 SVN 주소, 배포 SVN 주소 연결 확인
                validateSvnConnections(devSvnUrl, dpSvnUrl, svnUsername, svnPassword);
            }

            return;
        }
        
        // 배포 SVN 주소 변경 여부 확인
        if (project.getDpSvnUrl().equals(dpSvnUrl)) {
            // 개발 SVN 주소, svnUsername, svnPassword 변경 여부 확인
            if (!project.getDevSvnUrl().equals(devSvnUrl) || !project.getSvnUsername().equals(svnUsername)
                || !project.getSvnPassword().equals(svnPassword)) {
                // 개발 SVN 주소, 배포 SVN 주소 연결 확인
                validateSvnConnections(devSvnUrl, dpSvnUrl, svnUsername, svnPassword);
            }
        } else {
            // 배포 SVN 주소 변경 작업
            updateDpSvnUrl(projectUpdateRequest, project, devSvnUrl, dpSvnUrl, svnUsername, svnPassword);
        }
    }

    /**
     * 배포 SVN 주소 변경 작업
     * @param projectUpdateRequest
     * @param project
     * @param devSvnUrl
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     */
    private void updateDpSvnUrl(
        ProjectUpdateRequest projectUpdateRequest, Project project, String devSvnUrl,
        String dpSvnUrl, String svnUsername, String svnPassword
    ) {
        // 개발 SVN 주소, 배포 SVN 주소 연결 확인
        validateSvnConnections(devSvnUrl, dpSvnUrl, svnUsername, svnPassword);
        
        // 배포 SVN 주소 변경에 따른 저장소 업데이트 및 프로젝트 폴더 정리
        updateDpSvnRepoAndProjectDir(project, dpSvnUrl, svnUsername, svnPassword);

        Long dcr = SvnRepositoryUtil.getLatestRevision(devSvnUrl, svnUsername, svnPassword);
        if (0 > dcr) {
            throw new BusinessException(ErrorCode.SVN_REVISION_NOT_FOUND);
        }
        projectUpdateRequest.setDcr(dcr);
    }

    /**
     * 개발 SVN 주소, 배포 SVN 주소 연결 확인
     * @param devSvnUrl
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     */
    private void validateSvnConnections(String devSvnUrl, String dpSvnUrl, String svnUsername, String svnPassword) {
        LOGGER.info("개발 / 배포 SVN 주소 연결 확인");

        if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_DEV_REPO_REFUSED);
        }
    
        if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_DP_REPO_REFUSED);
        }
    }

    /**
     * 배포 SVN 주소 변경에 따른 저장소 업데이트 및 프로젝트 폴더 정리
     * @param project
     * @param dpSvnUrl
     * @param svnUsername
     * @param svnPassword
     */
    private void updateDpSvnRepoAndProjectDir(Project project, String dpSvnUrl, String svnUsername, String svnPassword) {
        String sdRootPath = sdInfoService.getSdRootPath();
        String pjtDirPath = Paths.get(sdRootPath, project.getPjtKey()).toString();
        
        // 프로젝트 폴더내 파일 및 폴더 삭제
        SdFileUtil.cleanDirectory(pjtDirPath);
        
        // 프로젝트 폴더에 배포 SVN 저장소 체크아웃(연결)
        checkoutDpSvnRepository(dpSvnUrl, svnUsername, svnPassword, pjtDirPath);
        
        // 배포 SVN 저장소에 trunk, staging, master 브랜치 생성
        createDpSvnBranches(dpSvnUrl, svnUsername, svnPassword);
        
        // 배포 SVN 저장소 update
        updateDpSvnRepository(dpSvnUrl, svnUsername, svnPassword, pjtDirPath);
    }
}
