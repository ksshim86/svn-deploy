package com.ks.sd.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SVN_DEV_REPO_REFUSED("S001", "개발 SVN 저장소 연결 실패"),
    SVN_DP_REPO_REFUSED("S002", "배포 SVN 저장소 연결 실패"),
    SVN_REPO_CHECKOUT_FAILED("S003", "SVN checkout 실패"),
    SVN_REVISION_NOT_FOUND("S004", "리비전 조회 실패"),
    SVN_BRANCH_CREATE_FAILED("S005", "SVN 브랜치 생성 실패"),
    SVN_UPDATE_FAILED("S006", "SVN 업데이트 실패"),

    SVR_SQL_ERROR("C001", "서버 오류"),

    SVR_CMM_ERROR("M001", "서버 오류"),
    SVR_MKDIR_FAILED("M002", "디렉토리 생성 실패"),

    HTTP_REQUEST_FAILED("H001", "HTTP 요청이 잘못됨"),

    INVALID_INPUT_VALUE("R001", "요청 파라미터 오류"),
    SAVE_TARGET_NOT_FOUND("R002", "저장 대상이 존재하지 않습니다."),
    UPDATE_TARGET_NOT_FOUND("R003", "수정 대상이 존재하지 않습니다."),
    DELETE_TARGET_NOT_FOUND("R004", "삭제 대상이 존재하지 않습니다."),

    PJT_NOT_FOUND("P001", "프로젝트가 존재하지 않습니다."),
    
    USER_NOT_FOUND("U001", "사용자가 존재하지 않습니다."),
    USER_ALREADY_EXISTS("U002", "사용자가 이미 존재합니다."),

    TEAM_NOT_FOUND("T001", "팀이 존재하지 않습니다."),

    ROLE_NOT_FOUND("R001", "권한이 존재하지 않습니다."),
    USER_ALREADY_HAS_ROLE("R002", "사용자가 이미 권한을 가지고 있습니다."),
    USER_NOT_HAS_ROLE("R003", "사용자가 권한을 가지고 있지 않습니다."),

    APPRS_NOT_FOUND("A001", "결재 절차가 존재하지 않습니다.")
    ;

    private final String code;
    private final String message;
}
