package com.midas.shootpointer.global.util.encrypt;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HmacSignerTest {

    @Test
    @DisplayName("Hmac-SHA256 알고리즘으로 서명을 생성한다")
    void generateSignature() throws Exception {
        // given
        String secretKey = "test-secret-key";
        String message = "1700000000:11111111-1111-1111-1111-111111111111:22222222-2222-2222-2222-222222222222:video.mp4";
        HmacSigner signer = new HmacSigner(secretKey);

        // when
        String signature = signer.getHmacSignature(message);

        // then
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        String expected = Base64.encodeBase64String(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));

        assertThat(signature).isEqualTo(expected);
    }
}
