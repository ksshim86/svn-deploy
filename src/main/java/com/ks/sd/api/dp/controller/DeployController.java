package com.ks.sd.api.dp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.dp.dto.DeploySaveRequest;
import com.ks.sd.api.dp.dto.DeployUpdateRequest;
import com.ks.sd.api.dp.dto.DeployView;
import com.ks.sd.api.dp.entity.Deploy;
import com.ks.sd.api.dp.service.DeployService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/deploy")
public class DeployController {
    @Autowired
    private DeployService deployService;

    @GetMapping(value="/undeleted")
    public ResponseEntity<List<DeployView>> getUndeletedDeploys() {
        final String UNDELETED = "N";

        List<DeployView> deployViews = deployService.getDeploysByDelYn(UNDELETED);
        
        return ResponseEntity.ok(deployViews);
    }

    @PostMapping
    public ResponseEntity<DeployView> saveDeploy(@RequestBody DeploySaveRequest deploySaveRequest) {
        Deploy saveDeploy = deployService.saveDeploy(deploySaveRequest);
        Deploy deploy = deployService.getDeployByDpNo(saveDeploy.getDpNo());
        DeployView deployView = DeployView.builder().deploy(deploy).build();

        return ResponseEntity.status(HttpStatus.CREATED).body(deployView);
    }

    @PutMapping("/{dpNo}")
    public ResponseEntity<DeployView> updateDeploy(
        @PathVariable Integer dpNo, @RequestBody DeployUpdateRequest deployUpdateRequest
    ) {
        Deploy updateDeploy = deployService.updatDeploy(dpNo, deployUpdateRequest);
        DeployView deployView = DeployView.builder().deploy(updateDeploy).build();

        return ResponseEntity.ok(deployView);
    }

    @DeleteMapping("/{dpNo}")
    public ResponseEntity<Void> deleteDeploy(@PathVariable Integer dpNo) {
        deployService.deleteDeploy(dpNo);

        return ResponseEntity.noContent().build();
    }
}
