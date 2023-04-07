package com.ks.sd.api.dp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.dp.dto.DeploySaveRequest;
import com.ks.sd.api.dp.dto.DeployUpdateRequest;
import com.ks.sd.api.dp.dto.DeployResponse;
import com.ks.sd.api.dp.service.DeployService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/deploys")
public class DeployController {
    @Autowired
    private DeployService deployService;

    @GetMapping
    public List<DeployResponse> searchDeploys(
        @RequestParam(required = false) Integer pjtNo,
        @RequestParam(required = false) String dpDiv,
        @RequestParam(required = false) String delYn,
        @RequestParam(required = false) String sort
    ) {
        List<DeployResponse> responses = deployService.searchDeploys(pjtNo, dpDiv, delYn, sort);
        
        return responses;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeployResponse saveDeploy(@RequestBody DeploySaveRequest deploySaveRequest) {
        return deployService.saveDeploy(deploySaveRequest);
    }

    @PutMapping("/{dpNo}")
    public DeployResponse updateDeploy(
        @PathVariable Integer dpNo, @RequestBody DeployUpdateRequest deployUpdateRequest
    ) {
        deployUpdateRequest.setDpNo(dpNo);

        return deployService.updatDeploy(deployUpdateRequest);
    }

    @DeleteMapping("/{dpNo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDeploy(@PathVariable Integer dpNo) {
        deployService.deleteDeploy(dpNo);
    }
}
