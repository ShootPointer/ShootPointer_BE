package com.midas.shootpointer.global.util.encrypt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EncryptConverterTest {

    private static final String TEST_KEY_HEX = "";

    @BeforeAll
    static void 헥사키_설정() throws Exception {
        java.lang.reflect.Field keyField = EncryptConverter.class.getDeclaredField("keyHex");
        keyField.setAccessible(true);
        keyField.set(null, TEST_KEY_HEX);
    }

    @Test
    @DisplayName("암호화 / 복호화 테스트")
    void 암호화_복호화_테스트() {
        EncryptConverter converter = new EncryptConverter();
        String original = "test1234test1234";

        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);

        assertThat(encrypted).isNotEqualTo(original);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    @DisplayName("null 입력 처리")
    void null_값() {
        EncryptConverter converter = new EncryptConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

}