package com.ks.sd.api.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.sys.entity.SdSystem;
import com.ks.sd.api.sys.service.SdSystemService;

@RestController
@RequestMapping("/api/sys")
public class SdSystemController {
    @Autowired
    private SdSystemService sdSystemService;

    @GetMapping
    public ResponseEntity<SdSystem> getSdSystem() {
        SdSystem sdSystem = sdSystemService.getSdSystem();

        return new ResponseEntity<>(sdSystem, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SdSystem> saveSdSystem(@RequestBody SdSystem saveSdSystem ) {
        SdSystem sdSystem = sdSystemService.saveSdSystem(saveSdSystem);

        return new ResponseEntity<>(sdSystem, HttpStatus.OK);
    }
}
