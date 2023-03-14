package com.ks.sd.api.pjt.service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.appr.repository.AppPrRepository;
import com.ks.sd.api.info.service.SdInfoService;
import com.ks.sd.api.pjt.dto.ProjectMngResponse;
import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
import com.ks.sd.api.pjt.dto.ProjectSaveResponse;
import com.ks.sd.api.pjt.dto.ProjectSearchRequest;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.pjt.dto.SubProjectMngResponse;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.repository.ProjectRepository;
import com.ks.sd.api.pjt.repository.SubProjectRepository;
import com.ks.sd.errors.ErrorCode;
import com.ks.sd.errors.exception.BusinessException;
import com.ks.sd.util.file.SdFileUtil;
import com.ks.sd.util.svn.SvnRepositoryUtil;

@Transactional
@Service
public class ProjectService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SvnRepositoryUtil.class);

    @Autowired
    private SdInfoService sdInfoService;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SubProjectRepository subProjectRepository;

    @Autowired
    private AppPrRepository appPrRepository;

    /**
     * 프로젝트 조회(서브 프로젝트 포함)
     */
    public List<ProjectMngResponse> getProjectMngs(ProjectSearchRequest projectSearchRequest) {
        List<ProjectMngResponse> projectMngResponses = new ArrayList<ProjectMngResponse>();

        Optional<List<Project>> optProjects = projectRepository.searchProjects(projectSearchRequest);

        optProjects.ifPresent(projects -> {
            projects.forEach(project -> {
                List<SubProjectMngResponse> subProjectMngResponses = new ArrayList<>();
                
                if (projectSearchRequest.isIncludeSubPjt()) {
                    Optional<List<SubProject>> optSubProjects = 
                        subProjectRepository.findByProjectAndDelYn(project, "N");
                    
                    optSubProjects.ifPresent(subProjects -> {
                        subProjects.forEach(subProject -> {
                            SubProjectMngResponse subProjectMngResponse
                                = SubProjectMngResponse.builder().subProject(subProject).build();
                                subProjectMngResponses.add(subProjectMngResponse);
                        });
                    });
                }
                
                ProjectMngResponse projectMngResponse
                    = ProjectMngResponse.builder().project(project).subPjtMngResponses(subProjectMngResponses).build();

                projectMngResponses.add(projectMngResponse);
            });
        });

        return projectMngResponses;
    }

    public ProjectMngResponse getProjectByPjtNo(Integer pjtNo) {
        Project project = 
            projectRepository.findById(pjtNo)
                .orElseThrow(() -> new BusinessException(ErrorCode.PJT_NOT_FOUND));

        return ProjectMngResponse.builder().project(project).build();
    }
    
    /**
     * 프로젝트 저장
     */
    public ProjectSaveResponse saveProject(ProjectSaveRequest projectSaveRequest) throws Exception {
        Project project = new Project();
        String devSvnUrl = projectSaveRequest.getDevSvnUrl();
        String dpSvnUrl = projectSaveRequest.getDpSvnUrl();
        String svnUsername = projectSaveRequest.getSvnUsername();
        String svnPassword = projectSaveRequest.getSvnPassword();
        String pjtKey = projectSaveRequest.getPjtKey();
        ErrorCode errorCode = null;

        LOGGER.debug("개발 SVN 주소 연결 확인");
        if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_DEV_REPO_REFUSED);
        }
        LOGGER.debug("배포 SVN 주소 연결 확인");
        if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_DP_REPO_REFUSED);
        }

        String sdRootPath = sdInfoService.getSdRootPath();
        String pjtDirPath = Paths.get(sdRootPath, pjtKey).toString();

        LOGGER.debug("프로젝트 디렉토리 생성: " + pjtDirPath);
        if (!SdFileUtil.mkdirs(pjtDirPath)) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }

        LOGGER.debug("프로젝트 디렉토리에 배포 SVN 주소 체크아웃");
        if (!SvnRepositoryUtil.checkout(dpSvnUrl, svnUsername, svnPassword, pjtDirPath)) {
            errorCode = ErrorCode.SVN_REPO_CHECKOUT_FAILED;
        }

        String orgBranchNm = "";
        String dstBranchNm = "/trunk";
        String message = "Creating trunk branch";

        if (errorCode == null) {
            LOGGER.debug("trunk branch 생성");
            if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                errorCode = ErrorCode.SVN_BRANCH_CREATE_FAILED;
            }
        }

        orgBranchNm = "/trunk";
        dstBranchNm = "/branches/staging";
        message = "Creating staging branch";

        if (errorCode == null) {
            LOGGER.debug("staging branch 생성");
            if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                errorCode = ErrorCode.SVN_BRANCH_CREATE_FAILED;
            }
        }

        orgBranchNm = "/branches/staging";
        dstBranchNm = "/branches/master";
        message = "Creating master branch";

        if (errorCode == null) {
            LOGGER.debug("master branch 생성");
            if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                errorCode = ErrorCode.SVN_BRANCH_CREATE_FAILED;
            }
        }

        long headRevision = -1;

        if (errorCode == null) {
            LOGGER.debug("배포 SVN UPDATE");
            if (!SvnRepositoryUtil.update(dpSvnUrl, svnUsername, svnPassword, pjtDirPath, headRevision)) {
                errorCode = ErrorCode.SVN_UPDATE_FAILED;
            }
        }

        if (errorCode == null) {
            LOGGER.debug("개발 SVN 최신 리비전 조회");
            Long dcr = SvnRepositoryUtil.getLatestRevision(devSvnUrl, svnUsername, svnPassword);
    
            if (0 > dcr) {
                errorCode = ErrorCode.SVN_REVISION_NOT_FOUND;
            }
    
            projectSaveRequest.setDcr(dcr);
            
            LOGGER.debug("프로젝트 정보 저장");
            project = projectRepository.save(projectSaveRequest.toEntity());
        }

        if (errorCode != null) {
            LOGGER.debug("오류 발생, 생성된 폴더 삭제");
            SdFileUtil.deleteDirectory(pjtDirPath);
            throw new BusinessException(errorCode);
        }

        return ProjectSaveResponse.builder().project(project).build();
    }
    
    /**
     * 프로젝트 수정
     */
    public ProjectSaveResponse updateProject(ProjectUpdateRequest projectUpdateRequest) {
        Project project = projectRepository.findById(
            projectUpdateRequest.getPjtNo()).orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));
        
        String devSvnUrl = projectUpdateRequest.getDevSvnUrl();
        String dpSvnUrl = projectUpdateRequest.getDpSvnUrl();
        String svnUsername = projectUpdateRequest.getSvnUsername();
        String svnPassword = projectUpdateRequest.getSvnPassword();

        LOGGER.debug("프로젝트 시작 여부 확인");
        if (project.getStartedYn().equals("N")) {
            LOGGER.debug("프로젝트 시작 X");

            LOGGER.debug("배포 SVN 주소 변경 여부 확인");
            if (project.getDpSvnUrl().equals(dpSvnUrl)) {
                LOGGER.debug("배포 SVN 주소 변경 X");

                LOGGER.debug("devSvnUrl, svnUsername, svnPassword 변경 여부 확인");
                if (!project.getDevSvnUrl().equals(devSvnUrl)
                    || !project.getSvnUsername().equals(svnUsername)
                    || !project.getSvnPassword().equals(svnPassword)
                ) {
                    LOGGER.debug("devSvnUrl, svnUsername, svnPassword 변경 O");
                    LOGGER.debug("개발 SVN 주소 연결 확인");
                    if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
                        throw new BusinessException(ErrorCode.SVN_DEV_REPO_REFUSED);
                    }
                    LOGGER.debug("배포 SVN 주소 연결 확인");
                    if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                        throw new BusinessException(ErrorCode.SVN_DP_REPO_REFUSED);
                    }
                }
            } else {
                LOGGER.debug("배포 SVN 주소 변경 O");
                LOGGER.debug("개발 SVN 주소 연결 확인");
                if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_DEV_REPO_REFUSED);
                }
                LOGGER.debug("배포 SVN 주소 연결 확인");
                if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_DP_REPO_REFUSED);
                }

                String sdRootPath = sdInfoService.getSdRootPath();
                String pjtDirPath = Paths.get(sdRootPath, project.getPjtKey()).toString();

                LOGGER.debug("배포 폴더내 파일 및 폴더 삭제");
                SdFileUtil.cleanDirectory(pjtDirPath);

                LOGGER.debug("배포 프로젝트 폴더에 배포 SVN 저장소 연결");
                if (!SvnRepositoryUtil.checkout(dpSvnUrl, svnUsername, svnPassword, pjtDirPath)) {
                    throw new BusinessException(ErrorCode.SVN_REPO_CHECKOUT_FAILED);
                }

                String orgBranchNm = "";
                String dstBranchNm = "/trunk";
                String message = "Creating trunk branch";

                LOGGER.debug("trunk branch 생성");
                if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                    throw new BusinessException(ErrorCode.SVN_BRANCH_CREATE_FAILED);
                }

                orgBranchNm = "/trunk";
                dstBranchNm = "/branches/staging";
                message = "Creating staging branch";

                LOGGER.debug("staging branch 생성");
                if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                    throw new BusinessException(ErrorCode.SVN_BRANCH_CREATE_FAILED);
                }

                orgBranchNm = "/branches/staging";
                dstBranchNm = "/branches/master";
                message = "Creating master branch";

                LOGGER.debug("master branch 생성");
                if (!SvnRepositoryUtil.createBranch(dpSvnUrl, svnUsername, svnPassword, orgBranchNm, dstBranchNm, message)) {
                    throw new BusinessException(ErrorCode.SVN_BRANCH_CREATE_FAILED);
                }

                LOGGER.debug("배포 SVN 저장소 최신 버전으로 업데이트");
                long headRevision = -1;
                SvnRepositoryUtil.update(dpSvnUrl, svnUsername, svnPassword, pjtDirPath, headRevision);
                
                LOGGER.debug("개발 SVN 저장소 최신 버전 조회");
                Long dcr = SvnRepositoryUtil.getLatestRevision(devSvnUrl, svnUsername, svnPassword);
        
                if (0 > dcr) {
                    throw new BusinessException(ErrorCode.SVN_REVISION_NOT_FOUND);
                }
        
                projectUpdateRequest.setDcr(dcr);
            }

            LOGGER.debug("프로젝트 시작 X - 프로젝트 정보 업데이트");
            project.beforeStartedUpdate(projectUpdateRequest);
        } else {
            LOGGER.debug("프로젝트 시작 O");
            LOGGER.debug("svnUsername, svnPassword 변경 여부 확인");
            if (!project.getSvnUsername().equals(svnUsername)
            || !project.getSvnPassword().equals(svnPassword)) {
                LOGGER.debug("svnUsername, svnPassword 변경 O");
                
                LOGGER.debug("개발 SVN 주소 연결 확인");
                if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_DEV_REPO_REFUSED);
                }
                LOGGER.debug("배포 SVN 주소 연결 확인");
                if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_DP_REPO_REFUSED);
                }
            }

            LOGGER.debug("프로젝트 시작 O - 프로젝트 정보 업데이트");
            project.afterStartedUpdate(projectUpdateRequest);
        }

        return ProjectSaveResponse.builder().project(project).build();
    }

    /**
     * 프로젝트 삭제
     * @param pjtNo
     */
    public void deleteProject(Integer pjtNo) {
        Project project = projectRepository.findById(pjtNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.DELETE_TARGET_NOT_FOUND));

        LOGGER.debug("프로젝트 시작 여부 확인");
        if (project.getStartedYn().equals("N")) {
            LOGGER.debug("프로젝트 시작 X");
            LOGGER.debug("프로젝트 디렉토리 삭제");
            String sdRootPath = sdInfoService.getSdRootPath();
            SdFileUtil.deleteDirectory(Paths.get(sdRootPath, project.getPjtKey()).toString());
            
            LOGGER.debug("승인 절차 완전 삭제");
            appPrRepository.deleteByProject(project);
            LOGGER.debug("프로젝트 정보 완전 삭제");
            subProjectRepository.deleteByProject(project);
            projectRepository.deleteByPjtNo(project.getPjtNo());
        } else {
            LOGGER.debug("프로젝트 시작 O");
            LOGGER.debug("프로젝트 정보 삭제");
            project.delete();
        }
    }
}
