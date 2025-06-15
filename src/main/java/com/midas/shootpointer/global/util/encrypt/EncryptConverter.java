package com.midas.shootpointer.global.util.encrypt;

import com.midas.shootpointer.global.annotation.CustomLog;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Convert
public class EncryptConverter implements AttributeConverter<String, String> {

    @Value("${encrypt.key}")
    private String keyHex;

    private static final String ALGORITHM = "AES";

    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(keyHex), ALGORITHM);

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByteArray(keyHex), ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decoded = Base64.getDecoder().decode(encrypted);

        return new String(cipher.doFinal(decoded));
    }

    @Override
    @CustomLog("== DB에 저장할 때 쓰는 암호화 ==")
    public String convertToDatabaseColumn(String attribute) {
        try {
            return attribute == null ? null : encrypt(attribute);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @CustomLog("== DB 데이터 복호화 ==")
    public String convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : decrypt(dbData);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
