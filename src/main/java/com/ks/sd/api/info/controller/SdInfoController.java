package com.ks.sd.api.info.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.info.entity.SdInfo;
import com.ks.sd.api.info.service.SdInfoService;

@RestController
@RequestMapping("/api/sd-info")
public class SdInfoController {
    @Autowired
    private SdInfoService sdInfoService;

    @GetMapping
    public SdInfo getSdInfo() {
        return sdInfoService.getSdInfo();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SdInfo saveSdInfo(@RequestBody SdInfo paramSdInfo) {
        return sdInfoService.saveSdInfo(paramSdInfo);
    }
}
