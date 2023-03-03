package com.ks.sd.api.pjt.dto;

import com.ks.sd.base.dto.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectSearchRequest extends BaseRequest {
    private boolean includeSubPjt;
}
