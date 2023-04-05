package com.ks.sd.schd;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import com.ks.sd.api.pjt.entity.Project;
import com.ks.sd.api.pjt.entity.SubProject;
import com.ks.sd.api.pjt.service.ProjectService;
import com.ks.sd.api.rev.dto.SdPathSaveRequest;
import com.ks.sd.api.rev.dto.SdRevisionSaveRequest;
import com.ks.sd.api.rev.entity.SdPath;
import com.ks.sd.api.rev.repository.SdPathRepository;
import com.ks.sd.api.rev.repository.SdRevisionRepository;
import com.ks.sd.util.svn.SvnRepositoryUtil;

@Service
public class SvnHistoryLoaderService {
    private final static Logger LOGGER = LoggerFactory.getLogger(SvnHistoryLoaderService.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SdRevisionRepository sdRevisionRepository;

    @Autowired
    private SdPathRepository sdPathRepository;
    
    @Transactional
    public void run(Integer pjtNo) {
        Project project = fetchProjectDetails(pjtNo);
        List<SubProject> subProjects = project.getSubProjects();

        LOGGER.info("Thread started for Project: {}({})", project.getPjtNm(), project.getPjtKey());
        subProjects.forEach(subProject -> {
            LOGGER.info("Sub Project: {}", subProject.getSubPjtNm());
        });

        LOGGER.info("Dev svn url of the Project: {}", project.getDevSvnUrl());

        Long latestRev = SvnRepositoryUtil.getLatestRevision(project.getDevSvnUrl(), project.getSvnUsername(), project.getSvnPassword());

        LOGGER.info("Starting collection from revision {} up to HEAD({}) revision", project.getDcr(), latestRev);

        if (!isNewRevisionToCollect(project.getDcr(), latestRev)) {
            LOGGER.info("No new revision to collect");
            return;
        }

        // 리비전 수집 시작 리비전 번호
        Long startRev = project.getDcr() + 1;

        int loopCnt = calculateLoopCount(startRev, latestRev);
        
        collectRevisions(project, startRev, latestRev, loopCnt, subProjects);

        LOGGER.info("Thread closed for Project: {}({})", project.getPjtNm(), project.getPjtKey());
    }

    // 프로젝트 상세 정보 조회
    private Project fetchProjectDetails(Integer pjtNo) {
        return projectService.getProjectByPjtNo(pjtNo);
    }

    // 새로 수집해야 할 리비전이 있는지 확인
    private boolean isNewRevisionToCollect(Long dcr, Long latestRev) {
        return (latestRev - dcr) > 0;
    }

    // 100개 일괄 처리하는 데 필요한 반복 횟수를 계산
    private int calculateLoopCount(Long dcr, Long latestRev) {
        final int batchSize = 100;
        int collectionRevCnt = (int) (latestRev - dcr + 1);
        return (int) Math.ceil((double) collectionRevCnt / batchSize);
    }

    // 리비전 수집
    private void collectRevisions(Project project, Long startRev, Long latestRev, int loopCnt, List<SubProject> subProjects) {
        for (int i = 0; i < loopCnt; i++) {
            Long endRev = startRev + 99;

            if (endRev > latestRev) {
                endRev = latestRev;
            }

            List<SVNLogEntry> logEntryList =
                SvnRepositoryUtil.fetchSvnLog(project.getDevSvnUrl(), project.getSvnUsername(), project.getSvnPassword(), startRev, endRev);
            
            processLogEntries(logEntryList, project, subProjects);

            startRev = endRev + 1;
        }

        project.updateDcr(latestRev);
    }

    // 리비전 정보 저장
    private void processLogEntries(List<SVNLogEntry> logEntryList, Project project, List<SubProject> subProjects) {
        logEntryList.forEach(logEntry -> {
            LocalDateTime revDt = LocalDateTime.ofInstant(logEntry.getDate().toInstant(), ZoneId.systemDefault());

            SdRevisionSaveRequest sdRevisionSaveRequest = SdRevisionSaveRequest.builder()
                .pjtNo(project.getPjtNo())
                .revNo((int) logEntry.getRevision())
                .author(logEntry.getAuthor())
                .revDt(revDt)
                .msg(logEntry.getMessage())
                .build();

            LOGGER.info(
                "revision: pjtNo={}, revNo={}, author={}, revDt={}, msg={}",
                sdRevisionSaveRequest.getPjtNo(),
                sdRevisionSaveRequest.getRevNo(),
                sdRevisionSaveRequest.getAuthor(),
                sdRevisionSaveRequest.getRevDt(),
                sdRevisionSaveRequest.getMsg().replace("\n", " ").replace("\r", " ")
            );

            sdRevisionRepository.save(sdRevisionSaveRequest.toEntity());

            List<SdPath> sdPaths = processChangedPaths(logEntry, project, subProjects);

            sdPathRepository.saveAll(sdPaths);
        });
    }

    // 파일 정보 저장
    private List<SdPath> processChangedPaths(SVNLogEntry logEntry, Project project, List<SubProject> subProjects) {
        AtomicInteger idx = new AtomicInteger(1);
        return logEntry.getChangedPaths().entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> createSdPathSaveRequest(entry.getValue(), logEntry, project, subProjects, idx.getAndIncrement()))
            .map(SdPathSaveRequest::toEntity)
            .collect(Collectors.toList());
    }

    // 파일 정보 저장 요청 생성
    private SdPathSaveRequest createSdPathSaveRequest(
        SVNLogEntryPath entryPath, SVNLogEntry logEntry, Project project, List<SubProject> subProjects, int ordr
    ) {
        String fullPath = entryPath.getPath();
        int lastSlashIndex = fullPath.lastIndexOf("/");
        String filePath = fullPath.substring(0, lastSlashIndex + 1);
        String fileNm = fullPath.substring(lastSlashIndex + 1);
        String kind = entryPath.getKind().toString().equals("dir") ? "D" : "F";

        SdPathSaveRequest sdPathSaveRequest = SdPathSaveRequest.builder()
            .pjtNo(project.getPjtNo())
            .revNo((int) logEntry.getRevision())
            .ordr(ordr)
            .action(Character.toString(entryPath.getType()))
            .kind(kind)
            .filePath(filePath)
            .fileNm(fileNm)
            .build();

        setCopyInfoIfPresent(entryPath, sdPathSaveRequest);

        int subPjtNo = checkSubProjectConditions(subProjects, filePath, fileNm);

        if (subPjtNo > 0) {
            sdPathSaveRequest.setSubPjtNo(subPjtNo);
        }

        LOGGER.info(
            "paths: pjtNo={}, revNo={}, ordr={}, action={}, kind={}, filePath={}, fileNm={}, copyFilePath={}, copyFileNm={}, subPjtNo={}",
            sdPathSaveRequest.getPjtNo(),
            sdPathSaveRequest.getRevNo(),
            sdPathSaveRequest.getOrdr(),
            sdPathSaveRequest.getAction(),
            sdPathSaveRequest.getKind(),
            sdPathSaveRequest.getFilePath(),
            sdPathSaveRequest.getFileNm(),
            sdPathSaveRequest.getCopyFilePath(),
            sdPathSaveRequest.getCopyFileNm(),
            sdPathSaveRequest.getSubPjtNo()
        );

        return sdPathSaveRequest;
    }

    // 파일 복사 정보 저장
    private void setCopyInfoIfPresent(SVNLogEntryPath entryPath, SdPathSaveRequest sdPathSaveRequest) {
        if (entryPath.getCopyPath() != null) {
            String copyFullPath = entryPath.getCopyPath();
            int lastSlashIndex = copyFullPath.lastIndexOf("/");
            sdPathSaveRequest.setCopyFilePath(copyFullPath.substring(0, lastSlashIndex + 1));
            sdPathSaveRequest.setCopyFileNm(copyFullPath.substring(lastSlashIndex + 1));
            sdPathSaveRequest.setCopyRevNo((int) entryPath.getCopyRevision());
        }
    }

    // 서브 프로젝트 조건 확인
    private int checkSubProjectConditions(List<SubProject> subProjects, String filePath, String fileNm) {
        for (SubProject subProject : subProjects) {
            String subPjtNm = subProject.getSubPjtNm();

            boolean condition1 = subPjtNm.equals(fileNm);
            boolean condition2 = filePath.startsWith("/") && filePath.substring(1).startsWith(subPjtNm + "/");

            if (condition1 || condition2) {
                return subProject.getSubPjtNo();
            }
        }

        return -1;
    }
}
