package com.midas.shootpointer.infrastructure.presigned.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PresignedUrlResponseTest {
    @Test
    @DisplayName("presignedUrl, expiredAt, signature, jobId 값을 입력하여 PresignedUrlResponse 객체를 생성합니다.")
    void PresignedUrlResponse(){
        //given
        String presignedUrl="pre-signed";
        String signature="signature";
        UUID jobId=UUID.randomUUID();
        String convertedJobId=jobId.toString();

        //when
        PresignedUrlResponse response=PresignedUrlResponse.of(presignedUrl,signature,convertedJobId);

        //then
        assertThat(response.presignedUrl()).isEqualTo(presignedUrl);
        assertThat(response.signature()).isEqualTo(signature);
        assertThat(response.jobId()).isEqualTo(convertedJobId);
    }
}