package com.midas.shootpointer.global.util.encrypt;

import com.midas.shootpointer.global.annotation.CustomLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class AesUtil {

    @Value("${encrypt.key}")
    private String keyHex;
    private static final String ALGORITHM = "AES";

    // HEX 문자열을 byte 배열로 변환
    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @CustomLog("== AES 암호화 ==")
    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(keyHex), ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    @CustomLog("== AES 복호화 ==")
    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(keyHex), ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encrypted);

        return new String(cipher.doFinal(decoded));
    }


}
