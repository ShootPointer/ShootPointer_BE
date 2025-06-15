package com.midas.shootpointer.global.util.encrypt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AesUtilTest {

    @Autowired
    AesUtil aesUtil;

    @Test
    void testEncryptAndDecrypt() throws Exception {
        String input = "test1234";

        System.out.println(input); // test1234
        String encrypted = aesUtil.encrypt(input);
        System.out.println(encrypted); // AES 암호화된 데이터
        String decrypted = aesUtil.decrypt(encrypted);
        System.out.println(decrypted); // 복호화한 값 test1234

        assertThat(decrypted).isEqualTo(input);
    }
}
