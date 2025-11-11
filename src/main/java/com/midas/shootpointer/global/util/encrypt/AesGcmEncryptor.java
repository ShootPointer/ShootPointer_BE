package com.midas.shootpointer.global.util.encrypt;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Slf4j
@Component

/**
 * AES GCM 알고리즘 적용
 */
public class AesGcmEncryptor {
    @Value("${opencv.pre-signed.secret.key}")
    private String secretKey;

    private static final String ALGO="AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH=12;
    private static final int GCM_TAG_LENGTH=128;

    private final SecureRandom secureRandom=new SecureRandom();
    private SecretKeySpec keySpec;

    @PostConstruct
    private void initializeKeySpec(){
        byte[] decodedKey=hexStringToByteArray(secretKey);
        this.keySpec=new SecretKeySpec(decodedKey,"AES");
    }

    /**
     * HEX 문자열 → 바이트 배열 변환 함수
     */
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                  + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String encrypt(String message){
        try {
            Cipher cipher=Cipher.getInstance(ALGO);

            //Random IV 생성
            byte[] IV=new byte[GCM_IV_LENGTH];
            this.secureRandom.nextBytes(IV);

            //GCMParameterSpec 생성
            GCMParameterSpec gcmParameterSpec=new GCMParameterSpec(GCM_TAG_LENGTH,IV);
            cipher.init(Cipher.ENCRYPT_MODE,this.keySpec,gcmParameterSpec);

            //암호화 수행
            byte[] cipherWithTag=cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

            //IV + Cipher + Tag 합치기
            byte[] combined=new byte[IV.length + cipherWithTag.length];
            System.arraycopy(IV,0,combined,0,IV.length);
            System.arraycopy(cipherWithTag,0,combined,IV.length,cipherWithTag.length);

            return Base64.encodeBase64URLSafeString(combined);

        } catch (Exception e) {
            log.error("AES-GCM encrypt 실패 : {}",e.getMessage());
            throw new RuntimeException("AES-GCM encrypt 실패");
        }
    }
}
