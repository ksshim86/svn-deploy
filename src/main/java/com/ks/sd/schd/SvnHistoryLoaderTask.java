package com.ks.sd.schd;

import java.util.function.BiConsumer;

import com.ks.sd.api.pjt.service.ProjectService;
import com.ks.sd.consts.SdConstants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SvnHistoryLoaderTask implements Runnable {
    private final Integer pjtNo;
    private final BiConsumer<Integer, Boolean> threadStatusUpdater;
    private final SvnHistoryLoaderService svnHistoryLoaderService;
    private final ProjectService projectService;

    @Override
    public void run() {
        try {
            // 리비전 수집 시작 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, SdConstants.COLLECTING);
            // 리비전 수집
            svnHistoryLoaderService.run(pjtNo);
        } finally {
            // 리비전 수집 완료 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, SdConstants.COMPLETION);
            // 스레드 상태 업데이트
            threadStatusUpdater.accept(pjtNo, true);
        }
    }
}
