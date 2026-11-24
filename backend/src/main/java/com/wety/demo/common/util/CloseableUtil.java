package com.wety.demo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * 工具类，用于Closeable/AutoCloseable的close调用，并忽略异常（出了异常我们也做不了什么）
 *
 * @author wety
 */
public class CloseableUtil {

    private static final Logger log = LoggerFactory.getLogger(CloseableUtil.class);

    private CloseableUtil() {
    }

    public static void closeIgnoreException(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error(closeable + ".close()调用异常！", e);
            }
        }
    }

    public static void closeIgnoreException(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error(closeable + ".close()调用异常！", e);
            }
        }
    }
}
