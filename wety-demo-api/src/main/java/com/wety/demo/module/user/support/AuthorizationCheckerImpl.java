package com.wety.demo.module.user.support;

import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.core.annotation.PermissionHolder;
import com.wety.demo.core.authorization.AuthorizationChecker;
import com.wety.demo.core.authorization.PermissionCache;
import com.wety.demo.module.user.constant.AccountTypeConst;
import com.wety.demo.module.user.service.PermissionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class AuthorizationCheckerImpl implements AuthorizationChecker {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationCheckerImpl.class);

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private PermissionService permissionService;

    @Override
    public boolean hasPermission(HttpServletRequest request, HttpServletResponse response) {
        String accountName = request.getParameter("ACCOUNTNAME");
        String path = request.getServletPath().replace("//", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        PermissionHolder requestingPermission = PermissionCache.getRequestMappingPermissionMapping().get(path);
        // 这个请求上没有配置Permission? 那就放行嘛
        if (requestingPermission == null) {
            return true;
        }

        String requestingCode = requestingPermission.getCode();
        Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByLoginName(accountName);
        // 用户未登录，无权访问
        if (!accountEntityOptional.isPresent()) {
            log.warn("用户未登录，无权访问");
            return false;
        }
        AccountEntity accountEntity = accountEntityOptional.get();
        if (AccountTypeConst.ADMIN.equals(accountEntity.getType())) {
            // 超级管理员拥有所有权限
            return true;
        }

        return hasPermission(accountEntity, requestingCode);
    }

    /**
     * 检查用户是否有请求的权限
     *
     * @param accountEntity
     * @param requestingCode
     * @return true表示有，false表示没有
     */
    private boolean hasPermission(AccountEntity accountEntity, String requestingCode) {
        List<String> permissionCodes = permissionService.listAccountPermission(accountEntity);
        if (CollectionUtils.isEmpty(permissionCodes)) {
            return false;
        }
        for (String permissionCode : permissionCodes) {
            // 这哥们儿有权限，break然后直接放行
            if (StringUtils.equals(requestingCode, permissionCode)) {
                return true;
            }
            PermissionHolder ownedPermissionHolder = PermissionCache.getPermissionCodePermissionMap().get(permissionCode);
            // 这哥们儿拥有的权限有依赖请求的权限，也break并放行
            if (ownedPermissionHolder != null
                  && !CollectionUtils.isEmpty(ownedPermissionHolder.getDependencyCodes())
                  && ownedPermissionHolder.getDependencyCodes().contains(requestingCode)) {
                return true;
            }
        }
        return false;
    }
}
