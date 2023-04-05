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

import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
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
    @Transactional
    void saveProjectTest() {
        String pjtKey = "TEST_REPO";
        String dpSvnUrl = "svn://192.168.0.186/sd-test-repo";
        Project project = new Project();

        ProjectSaveRequest projectSaveRequest = new ProjectSaveRequest();
        projectSaveRequest.setPjtKey(pjtKey);
        projectSaveRequest.setPjtNm("테스트프로젝트");
        projectSaveRequest.setDevSvnUrl("svn://192.168.0.186/prj-siis-ptlReport");
        projectSaveRequest.setDpSvnUrl(dpSvnUrl);
        projectSaveRequest.setSvnUsername("svndeploy");
        projectSaveRequest.setSvnPassword("svndeploy1!");
        projectSaveRequest.setDcr((long) 0);
 
        Project saveProjevct = projectRepository.save(projectSaveRequest.toEntity());
        System.out.println(saveProjevct.toString());
        
        project = projectRepository.findById(saveProjevct.getPjtNo()).get();
        System.out.println(project.getDelYn() + ", " + project.getStartedYn());

    }

    // @Test
    // void saveSubProjectTest() {
    //     Project project = projectRepository.findById(3).get();

    //     subProjectRepository.save(
    //         SubProject.builder()
    //             .project(project)
    //             .subPjtNm("smp-pjt-nm-1")
    //             .build()
    //     );
    // }

    // @Transactional
    // @Test
    // void getProject() {
    //     Project project = projectRepository.findById(1).get();
    //     System.out.println(project.toString());
    // }

    // @Transactional
    // @Test
    // void getSubProject() {
    //     Project project = projectRepository.findById(2).get();

    //     Optional<List<SubProject>> list = subProjectRepository.findByProjectAndDelYn(
    //         project,
    //         "N"
    //     );
    //     System.out.println(list.isPresent());
    // }

    // @Test
    // void beforeStartedUpdateTest() throws Exception {
    //     // ProjectSaveRequest projectSaveRequest = new ProjectSaveRequest(
    //     //     "PTL_REPORT",
    //     //     "리포트",
    //     //     "svn://192.168.0.186/prj-siis-ptlReport",
    //     //     "svn://192.168.0.186/prj-siis-ptlReport-deploy",
    //     //     "svndeploy",
    //     //     "svndeploy1!",
    //     //     (long) 0
    //     // );
        
    //     // ProjectSaveResponse projectSaveResponse = projectService.saveProject(projectSaveRequest);
        
    //     // Project project = projectRepository.findById(projectSaveResponse.getPjtNo()).get();
        
    //     // System.out.println(project.getStartedYn());
    //     // ProjectUpdateRequest projectUpdateRequest = new ProjectUpdateRequest(
    //     //     project.getPjtNo(),
    //     //     project.getPjtNm(),
    //     //     project.getDevSvnUrl(),
    //     //     project.getDpSvnUrl(),
    //     //     project.getSvnUsername(),
    //     //     project.getSvnPassword() + "!",
    //     //     (long) 0,
    //     //     "N",
    //     //     "N",
    //     //     "N"
    //     // );

    //     // projectService.updateProject(projectUpdateRequest);
    // }

    // @AfterEach
    // void after() throws Exception {
    //     projectRepository.deleteAll();
    //     SdFileUtil.cleanDirectory("C:\\deploy");
    // }
}
