package com.ks.sd.api.rev.dto;

import java.time.LocalDateTime;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.rev.entity.SdRevision;
import com.ks.sd.api.rev.entity.SdRevision.SdRevisionId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SdRevisionSaveRequest {
    Integer pjtNo;
    Integer revNo;
    String author;
    LocalDateTime revDt;
    String msg;

    public SdRevision toEntity() {
        return 
            SdRevision.builder()
                .id(SdRevisionId.builder().pjtNo(pjtNo).revNo(revNo).build())
                .project(Project.builder().pjtNo(pjtNo).build())
                .author(author)
                .revDt(revDt)
                .msg(msg)
                .build();
    }
}
