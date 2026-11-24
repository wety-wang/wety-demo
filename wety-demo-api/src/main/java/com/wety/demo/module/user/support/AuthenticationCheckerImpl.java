package com.wety.demo.module.user.support;

import cn.hutool.crypto.digest.MD5;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountSessionEntity;
import com.wety.demo.common.entity.AccountSessionEntityRepository;
import com.wety.demo.core.authentication.AuthenticationChecker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

/**
 * 封装请求认证校验逻辑
 *
 * @author wety
 */
@Service
public class AuthenticationCheckerImpl implements AuthenticationChecker<AccountEntity> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationCheckerImpl.class);

    @Value("${application.authentication.session.timeoutmillis}")
    private Long loginSessionTimeoutMillis;

    @Value("${application.authentication.request.timestamp.tolerance-millis:300000}")
    private Long requestTimestampToleranceMillis;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private AccountSessionEntityRepository accountSessionRepository;

    @Override
    public AccountEntity checkAuthentication(HttpServletRequest request) {
        String path = request.getServletPath();
        String accountName = request.getParameter("ACCOUNTNAME");
        String timestamp = request.getParameter("TIMESTAMP");
        String clientSign = request.getParameter("SIGN");

        Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByLoginName(accountName);
        if (!accountEntityOptional.isPresent()) {
            return null;
        }
        AccountEntity accountEntity = accountEntityOptional.get();

        Optional<AccountSessionEntity> accountSessionEntityOptional = accountSessionRepository.findFirstByAccountCodeOrderByIdDesc(accountEntity.getCode());
        if (!accountSessionEntityOptional.isPresent()) {
            log.error("认证失败, Session不存在:{}", path);
            return null;
        }
        AccountSessionEntity accountSessionEntity = accountSessionEntityOptional.get();
        Date updateTime = accountSessionEntity.getUpdateTime();
        if(updateTime != null) {
            long noActionDuration = System.currentTimeMillis() - updateTime.getTime();
            if(noActionDuration > loginSessionTimeoutMillis) {
                log.error("会话超时:{}", accountName);
                return null;
            }
        }

        String serverToken = accountSessionEntity.getToken();

        // 校验时间戳容忍窗口，防重放攻击（TIMESTAMP 为空或超出容忍范围则拒绝）
        try {
            long reqTs = Long.parseLong(timestamp);
            long delta = Math.abs(System.currentTimeMillis() - reqTs);
            if (delta > requestTimestampToleranceMillis) {
                log.warn("请求时间戳超出容忍范围: {}ms，path: {}", delta, path);
                return null;
            }
        } catch (Exception e) {
            log.warn("请求时间戳解析失败: {}，path: {}", timestamp, path);
            return null;
        }

        // 拼接待签名字符串并用服务端TOKEN签名
        String strToBeSign = path + "?ACCOUNTNAME=" + accountName + "&TIMESTAMP=" + timestamp + "&TOKEN=" + serverToken;

        String serverSign = MD5.create().digestHex(strToBeSign);
        if (!StringUtils.equals(clientSign, serverSign)) {
            log.warn("请求签名校验失败");
            return null;
        }
        accountSessionEntity.setUpdateTime(new Date());
        accountSessionRepository.save(accountSessionEntity);
        return accountEntity;
    }

}
