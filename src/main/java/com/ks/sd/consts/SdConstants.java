package com.ks.sd.consts;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class SdConstants {
    /**
     * deletedYn(삭제됨)
     */
    public final static String DELETED = "Y";
    /**
     * deletedYn(삭제안됨)
     */
    public final static String UNDELETED = "N";

    /**
     * dpSt(배포중)
     */
    public final static String ACTIVE = "Y";

    /**
     * dpSt(배포중지)
     */
    public final static String INACTIVE = "N";

    /**
     * startedYn(시작)
     */
    public final static String STARTED = "Y";
    /**
     * startedYn(중지)
     */
    public final static String STOPPED = "N";
    /**
     * rcsSt(수집중)
     */
    public final static String COLLECTING = "Y";
    /**
     * rcsSt(완료)
     */
    public final static String COMPLETION = "N";
}
