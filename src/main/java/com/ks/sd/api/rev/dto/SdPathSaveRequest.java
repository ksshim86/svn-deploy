package com.ks.sd.api.rev.dto;

import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.entity.SdPath.SdPathId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SdPathSaveRequest {
    private Integer pjtNo;
    private Integer revNo;
    private Integer subPjtNo;
    private Integer ordr;
    private String action;
    private String kind;
    private String filePath;
    private String fileNm;
    private Integer copyRevNo;
	private String copyFilePath;
	private String copyFileNm;

    public SdPath toEntity() {
        return 
            SdPath.builder()
                .id(SdPathId.builder().pjtNo(pjtNo).revNo(revNo).ordr(ordr).build())
                .subPjtNo(subPjtNo)
                .action(action)
                .kind(kind)
                .filePath(filePath)
                .fileNm(fileNm)
                .copyRevNo(copyRevNo)
                .copyFilePath(copyFilePath)
                .copyFileNm(copyFileNm)
                .build();
    }
}
