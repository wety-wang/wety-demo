package com.wety.demo.core.datascope;

import com.wety.demo.common.constant.GlobalReturnCodeEnum;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.core.misc.AppThreadLocal;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;

/**
 * 单条数据权限校验器。
 *
 * <p>供详情查询、修改、删除、启停、重置密码等单条业务操作调用，防止通过直接传入目标编码发生水平越权。</p>
 */
@Component
public class DataScopeChecker {

    /** 默认部门属性名占位。 */
    private static final String DEFAULT_DEPT_FIELD = "deptCode";

    /** 默认用户属性名占位。 */
    private static final String DEFAULT_USER_FIELD = "createUser";

    @Resource
    private DataScopeResolver dataScopeResolver;

    /**
     * 判断当前登录账号是否有权访问指定归属部门或归属人的数据。
     *
     * @param deptCode      目标数据所属部门编码
     * @param ownerUserCode 目标数据归属人（或目标账号自身）编码
     * @return true 表示允许访问，false 表示无权访问
     */
    public boolean isAccessible(String deptCode, String ownerUserCode) {
        DataScopeContext context = resolveActiveContext();
        if (context == null) {
            return false;
        }
        if (context.isAll()) {
            return true;
        }
        if (StringUtils.isNotBlank(deptCode)
                && !CollectionUtils.isEmpty(context.getAllowedDeptCodes())
                && context.getAllowedDeptCodes().contains(deptCode)) {
            return true;
        }
        return context.isIncludeSelf()
                && StringUtils.isNotBlank(ownerUserCode)
                && ownerUserCode.equals(context.getCurrentUserCode());
    }

    /**
     * 断言当前登录账号有权访问目标数据，否则设置 402 返回码并抛出 {@link CustomException}。
     *
     * @param deptCode      目标数据所属部门编码
     * @param ownerUserCode 目标数据归属人（或目标账号自身）编码
     * @throws CustomException 超出数据权限范围时抛出
     */
    public void assertAccessible(String deptCode, String ownerUserCode) {
        if (!isAccessible(deptCode, ownerUserCode)) {
            AppThreadLocal.getInstance().setReturnCode(GlobalReturnCodeEnum.ACCESS_DENIED.getCode());
            throw new CustomException("无权操作该数据，超出当前账号数据权限范围", 402);
        }
    }

    /**
     * 优先复用当前线程已绑定的上下文；若当前方法未标注 {@link DataScope}，则即时解析当前登录账号的数据权限。
     */
    private DataScopeContext resolveActiveContext() {
        AppThreadLocal threadLocal = AppThreadLocal.getInstance();
        DataScopeContext context = threadLocal.getDataScopeContext();
        if (context != null) {
            return context;
        }
        AccountEntity currentAccount = threadLocal.getCurrentAccount();
        return dataScopeResolver.resolve(currentAccount, DEFAULT_DEPT_FIELD, DEFAULT_USER_FIELD);
    }
}
