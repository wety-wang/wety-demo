package com.wety.demo.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * {@link SM2Utils} 单元测试。
 *
 * <p>验证在 JDK 21 环境下通过 Nashorn 引擎执行 SM2 加密与解密的往返一致性。</p>
 */
class SM2UtilsTest {

    /**
     * 验证普通密码明文经 SM2 加密后再解密可准确还原。
     */
    @Test
    void encryptAndDecrypt_shouldRoundTripAsciiPlaintext() {
        String plaintext = "Demo@2026#Test";

        String cipherText = SM2Utils.encrypt(plaintext);
        assertNotNull(cipherText);
        assertFalse(cipherText.isEmpty());
        assertNotEquals(plaintext, cipherText);

        String decrypted = SM2Utils.decrypt(cipherText);
        assertEquals(plaintext, decrypted);
    }

    /**
     * 验证包含中文及特殊字符的明文在多次 SM2 加密后均能正确解密还原。
     */
    @Test
    void encryptAndDecrypt_shouldRoundTripUnicodePlaintextAcrossMultipleEncryptions() {
        String plaintext = "测试明文_SM2!2026";

        String firstCipher = SM2Utils.encrypt(plaintext);
        String secondCipher = SM2Utils.encrypt(plaintext);
        assertNotNull(firstCipher);
        assertNotNull(secondCipher);
        assertNotEquals(firstCipher, secondCipher);

        assertEquals(plaintext, SM2Utils.decrypt(firstCipher));
        assertEquals(plaintext, SM2Utils.decrypt(secondCipher));
    }
}
