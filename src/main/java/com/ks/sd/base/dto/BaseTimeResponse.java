package com.ks.sd.base.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BaseTimeResponse {
    private LocalDateTime createdDt;
    private LocalDateTime updatedDt;
}
