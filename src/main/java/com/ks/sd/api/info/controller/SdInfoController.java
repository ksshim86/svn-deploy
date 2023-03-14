package com.ks.sd.api.info.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.info.entity.SdInfo;
import com.ks.sd.api.info.service.SdInfoService;

@RestController
@RequestMapping("/api/info")
public class SdInfoController {
    @Autowired
    private SdInfoService sdInfoService;

    @GetMapping
    public ResponseEntity<SdInfo> getSdSystem() {
        SdInfo sdSystem = sdInfoService.getSdSystem();

        return new ResponseEntity<>(sdSystem, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SdInfo> saveSdInfo(@RequestBody SdInfo paramSdInfo) {
        SdInfo sdInfo = sdInfoService.saveSdSystem(paramSdInfo);

        return new ResponseEntity<>(sdInfo, HttpStatus.CREATED);
    }
}
