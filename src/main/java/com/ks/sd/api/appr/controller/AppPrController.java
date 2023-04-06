package com.ks.sd.api.appr.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks.sd.api.appr.dto.AppPrUpdateRequest;
import com.ks.sd.api.appr.dto.AppPrResponse;
import com.ks.sd.api.appr.service.AppPrService;

@RestController
@RequestMapping("/api/projects/{pjtNo}/roles/app-prs")
public class AppPrController {
    @Autowired
    private AppPrService appPrService;

    @GetMapping
    public List<AppPrResponse> getAppPrsByPjtNo(@PathVariable Integer pjtNo) {
        return appPrService.getAppPrsByPjtNo(pjtNo);
    }

    @PutMapping
    public AppPrResponse updateUseYnByPjtNoAndOrdr(
        @PathVariable Integer pjtNo,
        @RequestBody AppPrUpdateRequest appPrUpdateRequest
    ) {
        appPrUpdateRequest.setPjtNo(pjtNo);

        return appPrService.updateUseYnByPjtNoAndOrdr(appPrUpdateRequest);
    }
}
