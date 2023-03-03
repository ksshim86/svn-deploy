package com.ks.sd.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SVN_REPO_ACCESS_REFUSED("S001", "SVN 저장소 연결 실패"),
    SVN_REPO_CHECKOUT_FAILED("S002", "SVN checkout 실패"),
    SVN_REVISION_NOT_FOUND("S003", "리비전 조회 실패"),

    SVR_SQL_ERROR("C001", "서버 오류"),

    SVR_CMM_ERROR("M001", "서버 오류"),
    SVR_MKDIR_FAILED("M002", "디렉토리 생성 실패"),

    HTTP_REQUEST_FAILED("H001", "HTTP 요청이 잘못됨"),

    INVALID_INPUT_VALUE("R001", "요청 파라미터 오류"),
    UPDATE_TARGET_NOT_FOUND("R002", "수정 대상이 존재하지 않습니다."),
    DELETE_TARGET_NOT_FOUND("R003", "삭제 대상이 존재하지 않습니다.")
    ;

    private final String code;
    private final String message;
}
