package com.wety.demo.common.constant;

/**
 * @author wety
 */
public class GlobalConst {

    private GlobalConst() {
    }

    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public static final String SUCCESS_UPPER_CASE = "SUCCESS";

    public static final String ON = "on";

    public static final String OFF = "off";

    /**
     * yes， 约定一个肯定值的时候用
     */
    public static final String YES = "yes";
    /**
     * no， 约定一个否定值的时候是用
     */
    public static final String NO = "no";

    public static final Integer DATA_STATUS_NORMAL = 1;
    public static final Integer DATA_STATUS_ABNORMAL = 0;

    public static final Integer DATA_STATUS_YES = 1;
    public static final Integer DATA_STATUS_NOT = 0;

    public static final Integer YESI = 1;
    public static final Integer NOT = 0;

    public static final Integer DATA_STATUS_ENABLE = 1;
    public static final Integer DATA_STATUS_DISABLE = 0;

    public static final Integer DATA_DEFAULT = 1;
    public static final Integer DATA_NOT_DEFAULT = 0;

    public static final Integer DATA_USE = 1;
    public static final Integer DATA_NOT_USE = 0;

    public static final String SORT_CREATE_TIME = "createTime";

    public static final String DELETE_ERROR = "删除发生错误,当前数据不存在，请刷新后重试!";

    public static final String FIND_ERROR = "当前数据不存在，请刷新后重试!";

    public static final String USE_ERROR = "删除发生错误,当前数据已被引用，请先解除引用!";

    public static final String IMAGE_CODE = "imageCode";

}
