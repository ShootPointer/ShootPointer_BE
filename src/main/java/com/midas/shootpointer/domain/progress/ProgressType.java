package com.midas.shootpointer.domain.progress;

import lombok.Getter;

@Getter
public enum ProgressType {
    UPLOADING, //원본 영상 업로드
    UPLOAD_COMPLETE, // 원본 영상 업로드 성공
    PROCESSING, //하이라이트 영상 생성 중
    COMPLETE //하이라이트 영상 생성 완료
}
