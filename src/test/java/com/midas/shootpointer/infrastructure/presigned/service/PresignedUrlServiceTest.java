package com.midas.shootpointer.infrastructure.presigned.service;

import com.midas.shootpointer.global.util.encrypt.HmacSigner;
import com.midas.shootpointer.global.util.file.FileValidator;
import com.midas.shootpointer.infrastructure.openCV.OpenCVProperties;
import com.midas.shootpointer.infrastructure.presigned.dto.FileMetadataRequest;
import com.midas.shootpointer.infrastructure.presigned.dto.PresignedUrlResponse;
import com.midas.shootpointer.infrastructure.websocket.WebSocketProgressPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PresignedUrlServiceTest {

    @Mock
    private HmacSigner signer;
    @Mock
    private FileValidator fileValidator;
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;
    @Mock
    private WebSocketProgressPublisher progressPublisher;

    private PresignedUrlService presignedUrlService;

    @Captor
    private ArgumentCaptor<String> messageCaptor;
    @Captor
    private ArgumentCaptor<String> keyCaptor;
    @Captor
    private ArgumentCaptor<String> valueCaptor;
    @Captor
    private ArgumentCaptor<Duration> durationCaptor;

    private static final long EXPIRE_MILLIS = 1_800_000L;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        OpenCVProperties openCVProperties = new OpenCVProperties();
        openCVProperties.setUrl("http://localhost:8888");

        OpenCVProperties.Expire expire = new OpenCVProperties.Expire();
        expire.setExpirationTime(EXPIRE_MILLIS);
        openCVProperties.setExpire(expire);

        OpenCVProperties.Api.Proxy proxy = new OpenCVProperties.Api.Proxy();
        proxy.setUploadVideo("upload");

        OpenCVProperties.Api api = new OpenCVProperties.Api();
        api.setProxy(proxy);
        openCVProperties.setApi(api);

        presignedUrlService = new PresignedUrlService(
                signer,
                openCVProperties,
                fileValidator,
                redisTemplate,
                progressPublisher,
                "upload:presigned"
        );
    }

    @Test
    @DisplayName("파일 검증을 통과하면 서명을 생성하고 Redis에 Job ID를 저장한다")
    void createPresignedUrl() {
        // given
        UUID memberId = UUID.randomUUID();
        FileMetadataRequest request = FileMetadataRequest.of("video.mp4", 1024L);
        when(signer.getHmacSignature(anyString())).thenReturn("signature");

        long now = Instant.now().getEpochSecond();

        // when
        PresignedUrlResponse response = presignedUrlService.createPresignedUrl(memberId, request);

        // then
        verify(fileValidator).isValidFileSize(request.fileSize());
        verify(fileValidator).isValidFileType(request.fileName());

        verify(signer).getHmacSignature(messageCaptor.capture());
        String expectedMessage = response.expiredAt() + ":" + memberId + ":" + response.jobId() + ":" + request.fileName();
        assertThat(messageCaptor.getValue()).isEqualTo(expectedMessage);

        verify(valueOperations).set(keyCaptor.capture(), valueCaptor.capture(), durationCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo("upload:presigned:" + memberId);
        assertThat(valueCaptor.getValue()).isEqualTo(response.jobId().toString());
        assertThat(durationCaptor.getValue()).isEqualTo(Duration.ofMillis(EXPIRE_MILLIS));

        assertThat(response.signature()).isEqualTo("signature");
        assertThat(response.presignedUrl()).isEqualTo("http://localhost:8888/upload?signature=signature");

        long ttlSeconds = Duration.ofMillis(EXPIRE_MILLIS).toSeconds();
        long lowerBound = now + ttlSeconds;
        assertThat(response.expiredAt()).isBetween(lowerBound, lowerBound + 2);
    }
}
