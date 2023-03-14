package com.ks.sd.api.appr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.appr.dto.AppPrUpdateRequest;
import com.ks.sd.api.appr.dto.AppPrView;
import com.ks.sd.api.appr.service.AppPrService;

@RestController
@RequestMapping("/api/app-pr")
public class AppPrController {
    @Autowired
    private AppPrService appPrService;

    @GetMapping("/pjt-no/{pjtNo}")
    public ResponseEntity<List<AppPrView>> getAppPrsByPjtNo(@PathVariable Integer pjtNo) {
        List<AppPrView> appPrViews = appPrService.getAppPrsByPjtNo(pjtNo);

        return ResponseEntity.ok(appPrViews);
    }

    @PutMapping("/pjt-no/{pjtNo}")
    public ResponseEntity<Void> updateAppPrByPjtNo(
        @PathVariable Integer pjtNo,
        @RequestBody AppPrUpdateRequest appPrUpdateRequest
    ) {
        appPrUpdateRequest.setPjtNo(pjtNo);
        appPrService.updateAppPrByPjtNo(appPrUpdateRequest);

        return ResponseEntity.noContent().build();
    }
}
