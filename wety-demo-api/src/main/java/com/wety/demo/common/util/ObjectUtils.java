package com.wety.demo.common.util;

import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author wety
 */
public class ObjectUtils {
    /**
     * 如果一个对象是BigDecimal类型，去掉小数点后面多于的0
     *
     * @param value
     * @return 等值的BigDecimal或者原对象
     */
    public static Object stripTrailingZerosIfBigDecimal(@Nullable Object value) {
        if (value instanceof BigDecimal) {
            return new BigDecimal(((BigDecimal) value).stripTrailingZeros().toPlainString());
        } else {
            return value;
        }
    }

    /**
     * BigDecimal不能直接使用equals判断两个值是否相等，同一个数值精度不同会视为不相等
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean equals(Object source, Object target) {
        if (source instanceof BigDecimal && target instanceof BigDecimal) {
            return ((BigDecimal) source).compareTo(((BigDecimal) target)) == 0;
        }
        return Objects.equals(source, target);
    }

    private ObjectUtils() {
    }

}
