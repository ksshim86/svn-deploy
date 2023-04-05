package com.ks.sd.schd;

import java.util.function.BiConsumer;

import com.ks.sd.api.pjt.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SvnHistoryLoaderTask implements Runnable {
    private final Integer pjtNo;
    private final BiConsumer<Integer, Boolean> threadStatusUpdater;
    private final SvnHistoryLoaderService svnHistoryLoaderService;
    private final ProjectService projectService;

    final String COMMENCEMENT = "Y";
    final String COMPLETION = "N";

    @Override
    public void run() {
        try {
            // 리비전 수집 시작 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, COMMENCEMENT);
            // 리비전 수집
            svnHistoryLoaderService.run(pjtNo);
        } finally {
            // 리비전 수집 완료 상태 업데이트
            projectService.updateProjectByRcsSt(pjtNo, COMPLETION);
            // 스레드 상태 업데이트
            threadStatusUpdater.accept(pjtNo, true);
        }
    }
}
