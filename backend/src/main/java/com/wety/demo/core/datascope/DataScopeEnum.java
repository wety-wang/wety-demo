package com.wety.demo.core.datascope;

/**
 * 角色数据权限范围枚举。
 *
 * <p>当用户拥有多个角色时，数据权限按并集（最大权限优先）原则生效。</p>
 */
public enum DataScopeEnum {

    /** 全部数据权限。 */
    ALL(1, "全部数据权限"),

    /** 自定义部门数据权限。 */
    CUSTOM(2, "自定义部门数据权限"),

    /** 本部门数据权限。 */
    DEPT(3, "本部门数据权限"),

    /** 本部门及以下数据权限。 */
    DEPT_AND_CHILD(4, "本部门及以下数据权限"),

    /** 仅本人数据权限。 */
    SELF(5, "仅本人数据权限");

    /** 持久化存储编码。 */
    private final int code;

    /** 中文名称说明。 */
    private final String label;

    DataScopeEnum(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 根据编码解析数据权限枚举；未知或空值时按最小权限原则回退为 {@link #SELF}。
     *
     * @param code 范围编码
     * @return 数据权限枚举
     */
    public static DataScopeEnum fromCode(Integer code) {
        if (code == null) {
            return SELF;
        }
        for (DataScopeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return SELF;
    }

    /**
     * 判断给定编码是否为合法的数据权限范围。
     *
     * @param code 范围编码
     * @return true 表示合法
     */
    public static boolean isValidCode(Integer code) {
        if (code == null) {
            return false;
        }
        for (DataScopeEnum value : values()) {
            if (value.code == code) {
                return true;
            }
        }
        return false;
    }
}
