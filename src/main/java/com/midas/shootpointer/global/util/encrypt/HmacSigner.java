package com.midas.shootpointer.global.util.encrypt;

import com.midas.shootpointer.global.common.ErrorCode;
import com.midas.shootpointer.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * HMAC-SHA256 서명 생성
 */
@Slf4j
@Component
public class HmacSigner {

    /*
    * Hmac-SHA256 알고리즘
     */
    private static final String SIGNATURE_ALGORITHM="HmacSHA256";

    private final String secretKey;

    public HmacSigner(@Value("${opencv.pre-signed.secret.key}") String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Hmac-SHA256 서명 생성 메서드
     * @param message 파일명 + 만료시간 데이터
     * @return 서명 완료된 데이터
     */
    public String getHmacSignature(String message){
        byte[]key=secretKey.getBytes();
        final SecretKeySpec secretKeySpec=new SecretKeySpec(key, SIGNATURE_ALGORITHM);

        try {
            Mac mac=Mac.getInstance(SIGNATURE_ALGORITHM);
            mac.init(secretKeySpec);
            return Base64.encodeBase64String(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
        }catch (Exception e){
            log.error("Hmac parsing error : {}",e.getMessage());
            throw new CustomException(ErrorCode.HMAC_CREATE_FAIL);
        }
    }
}
