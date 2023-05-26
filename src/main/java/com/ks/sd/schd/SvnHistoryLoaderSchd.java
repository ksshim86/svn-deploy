package com.ks.sd.schd;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ks.sd.api.pjt.dto.ProjectResponse;
import com.ks.sd.api.pjt.service.ProjectService;
import com.ks.sd.consts.SdConstants;

@Component
public class SvnHistoryLoaderSchd {
    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SvnHistoryLoaderService svnHistoryLoaderService;
    
    private final ConcurrentHashMap<Integer, Boolean> projectThreadStatus = new ConcurrentHashMap<>();

    // @Scheduled(fixedRate = 30000)
    public void performTask() {
        // 삭제되지 않고, 프로젝트가 시작되었고, 배포가 진행중이지 않고, 리비전 수집이 시작이 아닌 프로젝트 조회
        // 리비전 수집 상태도 확인하는 이유는 서브 프로젝트 등록 시, 리비전 수집 상태를 시작으로 업데이트 하기 때문에
        List<ProjectResponse> projectResponses =
            projectService.getProjectsByDelYnAndDpStAndStartedYnAndRcsSt(
                SdConstants.UNDELETED, SdConstants.INACTIVE, SdConstants.STARTED, SdConstants.COMPLETION
            );

        // 프로젝트별 스레드 실행
        projectResponses.stream().forEach(projectMngResponse -> {
            Integer pjtNo = projectMngResponse.getPjtNo();
            // 스레드가 실행중이지 않거나, 스레드가 종료된 경우에만 실행
            if (
                !projectThreadStatus.containsKey(pjtNo) 
                || projectThreadStatus.get(pjtNo)
            ) {
                // 스레드 생성
                SvnHistoryLoaderTask svnHistoryLoaderTask 
                    = new SvnHistoryLoaderTask(pjtNo, this::updateThreadStatus, svnHistoryLoaderService, projectService);
                // 스레드 상태 저장
                projectThreadStatus.put(pjtNo, false);
                // 스레드 실행
                executorService.submit(svnHistoryLoaderTask);
            }
        });
    }

    // 스레드 상태 업데이트
    private void updateThreadStatus(Integer pjtNo, Boolean isCompleted) {
        projectThreadStatus.put(pjtNo, isCompleted);
    }
}