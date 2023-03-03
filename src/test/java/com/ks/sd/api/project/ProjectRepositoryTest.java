package com.ks.sd.api.project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ks.sd.api.pjt.dto.ProjectMngResponse;
import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
import com.ks.sd.api.pjt.dto.ProjectSaveResponse;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.repository.ProjectRepository;
import com.ks.sd.api.pjt.repository.SubProjectRepository;
import com.ks.sd.api.pjt.service.ProjectService;
import com.ks.sd.util.file.SdFileUtil;

@ActiveProfiles("local")
@SpringBootTest
public class ProjectRepositoryTest {
    @Autowired
    ProjectService projectService;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    SubProjectRepository subProjectRepository;

    @Test
    void saveProjectTest() {
        String pjtKey = "SAMPLE_KEY";
        String dpSvnUrl = "svn://192.168.0.186/prj-siis-sis-deploy";

        Project project = 
            Project.builder()
                .pjtKey(pjtKey)
                .pjtNm("샘플프로젝트")
                .devSvnUrl("svn://192.168.0.186/prj-siis-sis")
                .dpSvnUrl(dpSvnUrl)
                .svnUsername("svndeploy")
                .svnPassword("svndeploy1!")
                .dcr((long) 0)
                .build();
        projectRepository.save(project);

        // projectRepository.findAll().forEach(p -> {
        //     System.out.println(p.toString());
        // });
        // SubProject subProject = SubProject.builder().project(project).subPjtNm("sub-project").build();
        // subProjectRepository.save(subProject);

        // Project project = projectRepository.findById(1).get();

        // System.out.println(project.getSubProjectList().size());
        // List<SubProject> list = subProjectRepository.findAllByProject(project);

        // System.out.println(list.get(0).getProject().getPjtKey());
        // System.out.println(list.get(0).getSubPjtNm());
    }

    @Test
    void saveSubProjectTest() {
        Project project = projectRepository.findById(3).get();

        subProjectRepository.save(
            SubProject.builder()
                .project(project)
                .subPjtNm("smp-pjt-nm-1")
                .build()
        );
    }

    @Transactional
    @Test
    void getProject() {
    }

    @Transactional
    @Test
    void getSubProject() {
        Project project = projectRepository.findById(2).get();

        Optional<List<SubProject>> list = subProjectRepository.findByProjectAndDelYn(
            project,
            "N"
        );
        System.out.println(list.isPresent());
    }

    @Test
    void beforeStartedUpdateTest() throws Exception {
        // ProjectSaveRequest projectSaveRequest = new ProjectSaveRequest(
        //     "PTL_REPORT",
        //     "리포트",
        //     "svn://192.168.0.186/prj-siis-ptlReport",
        //     "svn://192.168.0.186/prj-siis-ptlReport-deploy",
        //     "svndeploy",
        //     "svndeploy1!",
        //     (long) 0
        // );
        
        // ProjectSaveResponse projectSaveResponse = projectService.saveProject(projectSaveRequest);
        
        // Project project = projectRepository.findById(projectSaveResponse.getPjtNo()).get();
        
        // System.out.println(project.getStartedYn());
        // ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest(
        //     project.getPjtNo(),
        //     project.getPjtNm(),
        //     project.getDevSvnUrl(),
        //     project.getDpSvnUrl(),
        //     project.getSvnUsername(),
        //     project.getSvnPassword() + "!",
        //     (long) 0,
        //     "N",
        //     "N",
        //     "N"
        // );

        // projectService.updateProject(projectUpdateRequest);
    }

    @AfterEach
    void after() throws Exception {
        projectRepository.deleteAll();
        SdFileUtil.cleanDirectory("C:\\deploy");
    }
}
