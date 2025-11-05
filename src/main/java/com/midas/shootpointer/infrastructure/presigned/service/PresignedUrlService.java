package com.midas.shootpointer.infrastructure.presigned.service;

import com.midas.shootpointer.global.util.encrypt.HmacSigner;
import com.midas.shootpointer.global.util.file.FileValidator;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.presigned.dto.FileMetadataRequest;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import com.midas.shootpointer.infrastructure.websocket.WebSocketProgressPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class PresignedUrlService {
    private final HmacSigner signer;
    /**
     * openCv server Url
     */
    private final String opeCvBaseUrl;

    /**
     * 만료 시간
     */
    private final long ttlMilliSeconds;

    private final String uploadProxyPath;

    private final String prefix;

    /**
     *  file 유효성 검증
     */
    private final FileValidator fileValidator;
    /**
     *  Redis Template
     */
    private final RedisTemplate<String,String> redisTemplate;


    private final WebSocketProgressPublisher progressPublisher;

    public PresignedUrlService(
            HmacSigner signer,
            OpenCVProperties openCVProperties,
            FileValidator fileValidator,
            RedisTemplate<String,String> redisTemplate,
            WebSocketProgressPublisher progressPublisher,
            @Value("${spring.data.redis.custom.upload.key.upload}") String prefix
    ){
        this.signer=signer;
        this.fileValidator=fileValidator;
        this.redisTemplate=redisTemplate;
        this.progressPublisher=progressPublisher;
        this.opeCvBaseUrl= Objects.requireNonNull(openCVProperties.getUrl(), "OpenCV 서버 URL이 설정되어야 합니다.");
        OpenCVProperties.Expire expire=Objects.requireNonNull(openCVProperties.getExpire(), "만료 시간이 설정되어야 합니다.");
        this.ttlMilliSeconds=expire.getExpirationTime();
        OpenCVProperties.Api api=Objects.requireNonNull(openCVProperties.getApi(), "OpenCV API 설정이 필요합니다.");
        OpenCVProperties.Api.Proxy proxy=Objects.requireNonNull(api.getProxy(), "OpenCV proxy 설정이 필요합니다.");
        this.uploadProxyPath=Objects.requireNonNull(proxy.getUploadVideo(), "영상 업로드 경로가 설정되어야 합니다.");
        this.prefix=Objects.requireNonNull(prefix, "Redis 키 prefix는 필수입니다.");
    }

    private String buildUploadUrl(String signature){
        String normalizedPath = uploadProxyPath.startsWith("/") ? uploadProxyPath : "/" + uploadProxyPath;
        return UriComponentsBuilder.fromHttpUrl(opeCvBaseUrl)
                .path(normalizedPath)
                .queryParam("signature", signature)
                .build()
                .toUriString();
    }

    private String buildRedisKey(UUID memberId){
        return prefix.endsWith(":") ? prefix + memberId : prefix + ":" + memberId;
    }

    public PresignedUrlResponse createPresignedUrl(UUID memberId, FileMetadataRequest request){
        Objects.requireNonNull(request, "파일 메타데이터 정보는 필수입니다.");
        UUID jobId=UUID.randomUUID();
        long expires= Instant.now().plusMillis(ttlMilliSeconds).getEpochSecond();

        /**
         * 1.파일 유효성 검증
         */
        fileValidator.isValidFileSize(request.fileSize());
        fileValidator.isValidFileType(request.fileName());

        String message=expires+":"+memberId+":"+jobId+":"+request.fileName();
        String signature=signer.getHmacSignature(message);

        /**
         * 2.Pre-signed Url 생성
         */
        String preSignedUrl=buildUploadUrl(signature);

        /**
         * 3.redis에 memberId:jobId  형태 저장 - TTL : 30분
         */
        redisTemplate.opsForValue()
                .set(buildRedisKey(memberId), String.valueOf(jobId), Duration.ofMillis(ttlMilliSeconds));

        return PresignedUrlResponse.of(preSignedUrl,expires,signature,jobId);
    }
}
