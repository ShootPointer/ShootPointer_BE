package com.midas.shootpointer.infrastructure.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 진횅 상황
 */

@Getter
@AllArgsConstructor
public enum PROGRESS_TYPE {
    UPLOAD_PROGRESS, //원본 영상 업로드 중
    PROCESSING, //영상->하이라이트 영상 처리 중
    COMPLETE //하이라이트 영상 생성 완료
}
