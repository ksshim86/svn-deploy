package com.ks.sd.api.pjt.service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private SubProjectRepository subProjectRepository;

    // 프로젝트 목록 조회(서브 프로젝트 포함)
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
    
    // 프로젝트 등록
    public ProjectSaveResponse saveProject(ProjectSaveRequest projectSaveRequest) throws Exception {
        Project project = new Project();
        String devSvnUrl = projectSaveRequest.getDevSvnUrl();
        String dpSvnUrl = projectSaveRequest.getDpSvnUrl();
        String svnUsername = projectSaveRequest.getSvnUsername();
        String svnPassword = projectSaveRequest.getSvnPassword();
        String pjtKey = projectSaveRequest.getPjtKey();

        if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_REPO_ACCESS_REFUSED);
        }

        if (!SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
            throw new BusinessException(ErrorCode.SVN_REPO_ACCESS_REFUSED);
        }

        // TODO: 시스템 개발시에 시스템 정보에서 조회하는 기능으로 변경
        String pjtDirPath = Paths.get("c:\\deploy", pjtKey).toString();

        if (!SdFileUtil.mkdirs(pjtDirPath)) {
            throw new BusinessException(ErrorCode.SVR_MKDIR_FAILED);
        }

        if (!SvnRepositoryUtil.checkout(dpSvnUrl, svnUsername, svnPassword, pjtDirPath)) {
            throw new BusinessException(ErrorCode.SVN_REPO_CHECKOUT_FAILED);
        }

        Long dcr = SvnRepositoryUtil.getLatestRevision(devSvnUrl, svnUsername, svnPassword);

        if (0 > dcr) {
            throw new BusinessException(ErrorCode.SVN_REVISION_NOT_FOUND);
        }

        projectSaveRequest.setDcr(dcr);
        project = projectRepository.save(projectSaveRequest.toEntity());

        return ProjectSaveResponse.builder().project(project).build();
    }
    
    // 프로젝트 수정
    public ProjectSaveResponse updateProject(ProjectUpdateRequest projectUpdateRequest) {
        Project project = projectRepository.findById(
            projectUpdateRequest.getPjtNo()).orElseThrow(() -> new BusinessException(ErrorCode.UPDATE_TARGET_NOT_FOUND));
        
        String devSvnUrl = projectUpdateRequest.getDevSvnUrl();
        String dpSvnUrl = projectUpdateRequest.getDpSvnUrl();
        String svnUsername = projectUpdateRequest.getSvnUsername();
        String svnPassword = projectUpdateRequest.getSvnPassword();

        // 프로젝트 시작 여부 확인
        if (project.getStartedYn().equals("N")) {
            // 프로젝트 시작 X

            // 배포 SVN 주소 변경 여부 확인
            if (project.getDpSvnUrl().equals(dpSvnUrl)) {
                // 배포 SVN 주소 변경 X

                // devSvnUrl, svnUsername, svnPassword 변경 여부 확인
                if (!project.getDevSvnUrl().equals(devSvnUrl)
                    || !project.getSvnUsername().equals(svnUsername)
                || !project.getSvnPassword().equals(svnPassword)) {
                    // svnUsername, svnPassword 변경 O
                    // 개발 SVN 주소 연결 확인 AND 배포 SVN 주소 연결 확인
                    if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)
                    || !SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                        throw new BusinessException(ErrorCode.SVN_REPO_ACCESS_REFUSED);
                    }
                }
            } else {
                // 배포 SVN 주소 변경 O

                // 개발 SVN 주소 연결 확인 AND 배포 SVN 주소 연결 확인
                if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)
                || !SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_REPO_ACCESS_REFUSED);
                }

                // TODO: 시스템 개발시에 시스템 정보에서 조회하는 기능으로 변경
                String pjtDirPath = Paths.get("c:\\deploy", project.getPjtKey()).toString();

                // 배포 폴더 삭제
                SdFileUtil.cleanDirectory(pjtDirPath);

                // 배포 프로젝트 폴더에 배포 SVN 저장소 연결
                if (!SvnRepositoryUtil.checkout(dpSvnUrl, svnUsername, svnPassword, pjtDirPath)) {
                    throw new BusinessException(ErrorCode.SVN_REPO_CHECKOUT_FAILED);
                }
                
                Long dcr = SvnRepositoryUtil.getLatestRevision(devSvnUrl, svnUsername, svnPassword);
        
                if (0 > dcr) {
                    throw new BusinessException(ErrorCode.SVN_REVISION_NOT_FOUND);
                }
        
                projectUpdateRequest.setDcr(dcr);
            }

            project.beforeStartedUpdate(projectUpdateRequest);
        } else {
            // 프로젝트 시작 O

            // svnUsername, svnPassword 변경 여부 확인
            if (!project.getSvnUsername().equals(svnUsername)
            || !project.getSvnPassword().equals(svnPassword)) {
                // svnUsername, svnPassword 변경 O
                
                // 개발 SVN 주소 연결 확인 AND 배포 SVN 주소 연결 확인
                if (!SvnRepositoryUtil.isConnect(devSvnUrl, svnUsername, svnPassword)
                || !SvnRepositoryUtil.isConnect(dpSvnUrl, svnUsername, svnPassword)) {
                    throw new BusinessException(ErrorCode.SVN_REPO_ACCESS_REFUSED);
                }
            }

            project.afterStartedUpdate(projectUpdateRequest);
        }

        return ProjectSaveResponse.builder().project(project).build();
    }

    public void deleteProject(Integer pjtNo) {
        Project project = projectRepository.findById(pjtNo)
            .orElseThrow(() -> new BusinessException(ErrorCode.DELETE_TARGET_NOT_FOUND));
        
        if (project.getStartedYn().equals("N")) {
            // 배포 폴더 삭제
            SdFileUtil.deleteDirectory(Paths.get("c:\\deploy", project.getPjtKey()).toString());
            // data 삭제
            subProjectRepository.deleteByProject(project);
            projectRepository.deleteByPjtNo(project.getPjtNo());
        } else {
            project.delete();
        }
    }
}
