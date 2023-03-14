package com.ks.sd.api.appr.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppPrId implements Serializable {
    private Integer appPrOrdr;
    private Integer project;
}
