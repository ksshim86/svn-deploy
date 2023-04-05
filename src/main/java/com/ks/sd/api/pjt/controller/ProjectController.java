package com.ks.sd.api.pjt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.appr.service.AppPrService;
import com.ks.sd.api.pjt.dto.ProjectMngResponse;
import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
import com.ks.sd.api.pjt.dto.ProjectSaveResponse;
import com.ks.sd.api.pjt.dto.ProjectSearchRequest;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.service.ProjectService;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private AppPrService appPrService;
    
    @GetMapping("/search")
    public ResponseEntity<List<ProjectMngResponse>> getProjectMngs(ProjectSearchRequest projectSearchRequest) {
        List<ProjectMngResponse> projectMngResponses = projectService.getProjectMngs(projectSearchRequest);

        return new ResponseEntity<>(projectMngResponses, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ProjectMngResponse> saveProject(@RequestBody ProjectSaveRequest projectSaveRequest) throws Exception {
        ProjectSaveResponse projectSaveResponse = projectService.saveProject(projectSaveRequest);
        appPrService.saveAppPrs(Project.builder().pjtNo(projectSaveResponse.getPjtNo()).build());
        ProjectMngResponse projectMngResponse =  projectService.getProjectMngResponseByPjtNo(projectSaveResponse.getPjtNo());
        
        return new ResponseEntity<>(projectMngResponse, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ProjectSaveResponse> updateProject(@RequestBody ProjectUpdateRequest projectUpdateRequest) {
        ProjectSaveResponse projectSaveResponse = projectService.updateProject(projectUpdateRequest);

        return new ResponseEntity<>(projectSaveResponse, HttpStatus.OK);
    }

    @DeleteMapping("{pjtNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Integer pjtNo) {
        projectService.deleteProject(pjtNo);
    }
}
