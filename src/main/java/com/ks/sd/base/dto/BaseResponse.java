package com.ks.sd.base.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BaseResponse extends BaseTimeResponse {
    private String delYn;
}
