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
import com.ks.sd.api.pjt.dto.ProjectResponse;
import com.ks.sd.api.pjt.dto.ProjectSaveRequest;
import com.ks.sd.api.pjt.dto.ProjectUpdateRequest;
import com.ks.sd.api.pjt.dto.ProjectWithSubProjectsResponse;
import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.service.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private AppPrService appPrService;
    
    @GetMapping("/with-sub-projects")
    public ResponseEntity<List<ProjectWithSubProjectsResponse>> getProjectsWithSubProjects() {
        List<ProjectWithSubProjectsResponse> responseList = projectService.getProjectsWithSubProjects();

        return new ResponseEntity<>(responseList, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ProjectResponse> saveProject(@RequestBody ProjectSaveRequest projectSaveRequest) throws Exception {
        ProjectResponse projectResponse = projectService.saveProject(projectSaveRequest);
        appPrService.saveAppPrs(Project.builder().pjtNo(projectResponse.getPjtNo()).build());
        
        return new ResponseEntity<>(projectResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{pjtNo}")
    public ResponseEntity<ProjectResponse> updateProject(
        @PathVariable Integer pjtNo, @RequestBody ProjectUpdateRequest projectUpdateRequest
    ) {
        projectUpdateRequest.setPjtNo(pjtNo);
        ProjectResponse projectResponse = projectService.updateProject(projectUpdateRequest);

        return new ResponseEntity<>(projectResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{pjtNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Integer pjtNo) {
        projectService.deleteProject(pjtNo);
    }
}
