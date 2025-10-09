package com.midas.shootpointer.infrastructure.presigned.service;

import com.midas.shootpointer.global.util.encrypt.HmacSigner;
import com.midas.shootpointer.global.util.file.FileValidator;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.presigned.dto.FileMetadataRequest;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PresignedUrlService {
    public PresignedUrlService(
            HmacSigner signer,
            OpenCVProperties openCVProperties,
            FileValidator fileValidator
    ){
        this.opeCvBaseUrl= openCVProperties.getUrl();
        this.ttlMilliSeconds=openCVProperties.getExpire().getExpirationTime();
        this.signer=signer;
        this.fileValidator=fileValidator;
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

    /**
     *  file 유효성 검증
     */
    private FileValidator fileValidator;

    public PresignedUrlResponse createPresignedUrl(UUID memberId, FileMetadataRequest request){
        UUID jobId=UUID.randomUUID();
        long expires= Instant.now().plusMillis(ttlMilliSeconds).getEpochSecond();

        /**
         * 파일 유효성 검증
         */
        fileValidator.isValidFileSize(request.fileSize());
        fileValidator.isValidFileType(request.fileName());

        String message=expires+":"+memberId+":"+jobId+":"+request.fileName();
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
