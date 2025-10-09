package com.midas.shootpointer.infrastructure.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 진횅 상황
 */

@Getter
@AllArgsConstructor
public enum PROGRESS_TYPE {
    UPLOAD_START,//원본 영상 업로드 시작
    UPLOAD_PROGRESS,//원본 영상 업로드 중
    PROCESSING_START,//하이라이트 영상 생성 시작
    PROCESSING, //영상->하이라이트 영상 처리 중
    COMPLETE //하이라이트 영상 생성 완료
}
