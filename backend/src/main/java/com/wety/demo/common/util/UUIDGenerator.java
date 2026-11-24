package com.wety.demo.common.util;

import java.util.UUID;

/**
 * UUID生成器
 *
 * @author wety
 */
public class UUIDGenerator {
    private UUIDGenerator() {
    }

    /**
     * 生成一个UUID字符串<br>
     * UUID.randomUUID().toString().replace("-", "")
     *
     * @return
     */
    public static String generatorUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
