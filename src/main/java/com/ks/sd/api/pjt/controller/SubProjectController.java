package com.ks.sd.api.pjt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.pjt.dto.SubProjectResponse;
import com.ks.sd.api.pjt.dto.SubProjectSaveRequest;
import com.ks.sd.api.pjt.service.SubProjectService;

@RestController
@RequestMapping("/api/projects")
public class SubProjectController {
    @Autowired
    private SubProjectService subProjectService;

    @GetMapping("/{pjtNo}/sub-projects")
    public ResponseEntity<List<SubProjectResponse>> getSubProjectsByPjtNo(@PathVariable Integer pjtNo) {
        List<SubProjectResponse> responses = subProjectService.getSubProjectsByPjtNo(pjtNo);
        
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/{pjtNo}/sub-projects")
    public ResponseEntity<SubProjectResponse> save(
        @PathVariable Integer pjtNo, @RequestBody SubProjectSaveRequest subProjectSaveRequest
    ) {
        subProjectSaveRequest.setPjtNo(pjtNo);
        SubProjectResponse subProjectMngResponse = subProjectService.save(subProjectSaveRequest);

        return new ResponseEntity<>(subProjectMngResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{pjtNo}/sub-projects/{subPjtNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer pjtNo, @PathVariable Integer subPjtNo) {
        // subProjectService.delete(pjtNo, subPjtNo);
    }
}
