package com.wety.demo.common.util;

import com.antherd.smcrypto.sm2.Sm2;

/**
 * SM2加密工具类
 *
 * @author wety
 */
public class SM2Utils {

    private SM2Utils() {
    }

    /**
     * 私钥，用来解密
     */
    public static final String PRIVATE_KEY = "84b748bc6ce1c6550e15da829296f15bf83121677cf18e1eee0c0993415196da";

    /**
     * 公钥，用来加密
     */
    public static final String PUBLIC_KEY = "0496801ca6cbe79ab75dc0a396e19d90cbaa248e1c1c990c49fce2912a73c0308862323dea0f96f3a8b4e5c19270a4c52242db5dbff9d1df593998ec442ec2b0a6";

    /**
     * 解密，如果内容并非使用SM2加密或者加密内容被篡改或者内容是普通内容，解密会异常
     * @param cipherData
     * @return
     */
    public static String decrypt(String cipherData) {
        return Sm2.doDecrypt(cipherData, PRIVATE_KEY, 0);
    }

    /**
     * 加密SM2
     *
     * @param plaintext 明文
     * @return 密文
     */
    public static String encrypt(String plaintext) {
        return Sm2.doEncrypt(plaintext, PUBLIC_KEY,0);
    }
}
