package com.ks.sd.api.pjt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.pjt.dto.SubProjectMngResponse;
import com.ks.sd.api.pjt.dto.SubProjectSaveRequest;
import com.ks.sd.api.pjt.dto.SubProjectSaveResponse;
import com.ks.sd.api.pjt.dto.SubProjectUpdateRequest;
import com.ks.sd.api.pjt.service.SubProjectService;

@RestController
@RequestMapping("/api/sub-project")
public class SubProjectController {
    @Autowired
    private SubProjectService subProjectService;

    @GetMapping
    @RequestMapping("/pjt-no/{pjtNo}")
    public ResponseEntity<List<SubProjectMngResponse>> getSubProjectsByPjtNo(@PathVariable Integer pjtNo) {
        List<SubProjectMngResponse> subProjectMngResponses = subProjectService.getSubProjects(pjtNo);
        
        return new ResponseEntity<>(subProjectMngResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SubProjectSaveResponse> save(@RequestBody SubProjectSaveRequest subProjectSaveRequest) {
        SubProjectSaveResponse subProjectMngResponse = subProjectService.save(subProjectSaveRequest);

        return new ResponseEntity<>(subProjectMngResponse, HttpStatus.CREATED);
    }
    
    @PutMapping
    public ResponseEntity<SubProjectSaveResponse> update(@RequestBody SubProjectUpdateRequest subProjectUpdateRequest) {
        SubProjectSaveResponse subProjectMngResponse = subProjectService.update(subProjectUpdateRequest);

        return new ResponseEntity<>(subProjectMngResponse, HttpStatus.OK);
    }
}
