package com.midas.shootpointer.infrastructure.presigned.service;

import com.midas.shootpointer.global.util.encrypt.HmacSigner;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PresignedUrlService {
    public PresignedUrlService(
            HmacSigner signer,
            OpenCVProperties openCVProperties
    ){
        this.opeCvBaseUrl= openCVProperties.getUrl();
        this.ttlMilliSeconds=openCVProperties.getExpire().getExpirationTime();
        this.signer=signer;
    }

    private HmacSigner signer;
    /**
     * openCv server Url
     */
    @Value("${opencv.url}")
    private String opeCvBaseUrl;

    /**
     * 만료 시간
     */
    @Value("${opencv.api.expiration_time}")
    private long ttlMilliSeconds;

    public PresignedUrlResponse createPresignedUrl(UUID memberId){
        long expires= Instant.now().plusMillis(ttlMilliSeconds).getEpochSecond();

        String message=expires+":"+memberId;
        String signature=signer.getHmacSignature(message);

        /**
         * Pre-signed Url 생성
         */
        String preSignedUrl=String.format("%s/upload?expires=%d&memberId=%s&signature=%s",
                    opeCvBaseUrl,expires,memberId,signature
        );

        return PresignedUrlResponse.of(preSignedUrl,expires,signature);
    }
}
