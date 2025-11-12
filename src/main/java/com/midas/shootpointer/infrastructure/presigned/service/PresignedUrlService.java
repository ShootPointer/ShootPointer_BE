package com.midas.shootpointer.infrastructure.presigned.service;

import com.midas.shootpointer.global.util.encrypt.AesGcmEncryptor;
import com.midas.shootpointer.global.util.file.FileValidator;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.presigned.dto.FileMetadataRequest;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class PresignedUrlService {
    public PresignedUrlService(
            AesGcmEncryptor encryptor,
            OpenCVProperties openCVProperties,
            FileValidator fileValidator,
            RedisTemplate<String,String> redisTemplate
    ){
        this.opeCvBaseUrl= openCVProperties.getUrl();
        this.ttlMilliSeconds=openCVProperties.getExpire().getExpirationTime();
        this.encryptor=encryptor;
        this.fileValidator=fileValidator;
        this.redisTemplate=redisTemplate;
    }

    private AesGcmEncryptor encryptor;
    /**
     * openCv server Url
     */
    @Value("${opencv.url}")
    private String opeCvBaseUrl;

    /**
     * 만료 시간
     */
    @Value("${opencv.expire.expiration-time}")
    private long ttlMilliSeconds;

    @Value("${spring.data.redis.custom.upload.key.upload}")
    private String prefix;

    /**
     *  file 유효성 검증
     */
    private FileValidator fileValidator;
    /**
     *  Redis Template
     */
    private RedisTemplate<String,String> redisTemplate;



    public PresignedUrlResponse createPresignedUrl(UUID memberId, FileMetadataRequest request){
        String jobId=UUID.randomUUID().toString().replaceAll("-","").substring(0,16); //16자로 감소
        long expires= Instant.now().plusMillis(ttlMilliSeconds).getEpochSecond();

        /**
         * 1.파일 유효성 검증
         */
        fileValidator.isValidFileSize(request.fileSize());
        fileValidator.isValidFileType(request.fileName());

        String message=expires+":"+memberId+":"+jobId;
        String signature=encryptor.encrypt(message);

        /**
         * 2.Pre-signed Url 생성
         */
        String preSignedUrl=String.format("%s/upload?signature=%s",opeCvBaseUrl,signature);

        /**
         * 3.redis에 memberId:jobId  형태 저장 - TTL : 30분
         */
        //pre-signed URL 만료 검증 -> OpenCv 에서 진행함.
        redisTemplate.opsForValue()
                .set(prefix+memberId, jobId, Duration.ofMillis(ttlMilliSeconds));

        return PresignedUrlResponse.of(preSignedUrl,signature,jobId);
    }
}
