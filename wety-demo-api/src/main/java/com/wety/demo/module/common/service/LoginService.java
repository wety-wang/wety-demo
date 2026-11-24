package com.wety.demo.module.common.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountSessionEntity;
import com.wety.demo.common.entity.AccountSessionEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.SM2Utils;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.common.util.VerifyCodeUtil;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.auditlog.service.LoginLogService;
import com.wety.demo.module.common.dto.EditPasswordDTO;
import com.wety.demo.module.common.dto.LoginDTO;
import com.wety.demo.module.common.vo.AccountVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    @Resource
    private AccountSessionEntityRepository accountSessionEntityRepository;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private LoginLogService loginLogService;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public AccountVo login(LoginDTO loginDTO, HttpSession session) {
        Assert.notNull(loginDTO.getLoginName(), "用户名不能为空，请重新确认");
        Assert.notNull(loginDTO.getPassword(), "密码不能为空，请重新确认");
        Assert.notNull(loginDTO.getCheckCode(), "验证码不能为空，请重新确认");

        // 验证验证码
        checkImage(loginDTO.getCheckCode(), session);

        AccountEntity accountEntity = checkAccount(loginDTO);
        loginLogService.addAuditLog(loginDTO.getLoginName(), accountEntity, !Objects.isNull(accountEntity));

        if (accountEntity == null) {
            return null;
        }

        // 校验通过后保存并返回
        String token = UUIDGenerator.generatorUUID();

        saveAccountSession(accountEntity, token);

        AccountVo accountVo = new AccountVo(accountEntity, token);
        if (StringUtils.isNotBlank(accountEntity.getPrimaryDeptCode())) {
            deptEntityRepository.findByCodeAndDeleted(accountEntity.getPrimaryDeptCode(), GlobalConst.NOT)
                    .map(DeptEntity::getName)
                    .ifPresent(accountVo::setPrimaryDeptName);
        }
        return accountVo;
    }

    private void saveAccountSession(AccountEntity accountEntity, String token) {
        Optional<AccountSessionEntity> sessionEntityOptional = accountSessionEntityRepository.findFirstByAccountCodeOrderByIdDesc(accountEntity.getCode());
        AccountSessionEntity accountSessionEntity = sessionEntityOptional.orElseGet(() -> {
            AccountSessionEntity entity = new AccountSessionEntity();
            entity.setAccountCode(accountEntity.getCode());
            return entity;
        });
        accountSessionEntity.setToken(token);
        accountSessionEntity.setCreateTime(new Date());
        accountSessionEntity.setUpdateTime(new Date());
        accountSessionEntityRepository.save(accountSessionEntity);
    }

    /**
     * 根据用户名和密码来校验用户登录是否有效（仅允许未删除且未禁用账号登录）。
     *
     * @param loginDTO 用户名和密码
     * @return 登录有效则返回账号信息，否则返回 null
     */
    private AccountEntity checkAccount(LoginDTO loginDTO) {
        Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByLoginNameAndDeleted(loginDTO.getLoginName(), GlobalConst.NOT);
        if (accountEntityOptional.isPresent()) {
            AccountEntity accountEntity = accountEntityOptional.get();
            // 解密前端传入的 SM2 密文
            String rawPassword = SM2Utils.decrypt(loginDTO.getPassword());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (GlobalConst.DATA_STATUS_YES.equals(accountEntity.getDisabled())) {
                AppThreadLocal.getInstance().setReturnCode("500");
                AppThreadLocal.getInstance().setReturnMessage("该账号已被禁用，请联系管理员");
                return null;
            }
            // 使用 BCrypt 进行密码匹配
            if (StringUtils.isNotBlank(accountEntity.getPassword()) && encoder.matches(rawPassword, accountEntity.getPassword())) {
                return accountEntity;
            }
        }
        AppThreadLocal.getInstance().setReturnCode("500");
        AppThreadLocal.getInstance().setReturnMessage("账号或密码错误");
        return null;
    }

    public void logout() {
        AccountEntity accountEntity = AppThreadLocal.getInstance().getCurrentAccount();
        log.info("{} 退出登录", accountEntity.getUserName());
        Optional<AccountSessionEntity> sessionEntity = accountSessionEntityRepository.findFirstByAccountCodeOrderByIdDesc(accountEntity.getCode());
        sessionEntity.ifPresent(accountSessionEntity -> accountSessionEntityRepository.delete(accountSessionEntity));
        loginLogService.updateLogoutTime();
    }

    /**
     * 修改密码
     *
     * @param editPasswordDTO 修改密码DTO
     */
    public void editPassword(EditPasswordDTO editPasswordDTO) {
        Assert.notNull(editPasswordDTO.getCurrentPassword(), "旧密码不能为空!");
        Assert.notNull(editPasswordDTO.getNewPassword(), "新密码不能为空!");
        AccountEntity accountEntity = AppThreadLocal.getInstance().getCurrentAccount();
        // 解密旧密码并进行校验
        String currentRawPassword = SM2Utils.decrypt(editPasswordDTO.getCurrentPassword());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean currentPasswordOk = StringUtils.isNotBlank(accountEntity.getPassword()) && encoder.matches(currentRawPassword, accountEntity.getPassword());
        if (!currentPasswordOk) {
            throw new CustomException("密码不正确，请重新输入！");
        }
        // 更新为 BCrypt 哈希
        String newRawPassword = SM2Utils.decrypt(editPasswordDTO.getNewPassword());
        String newHash = encoder.encode(newRawPassword);
        accountEntityRepository.updatePasswordByCode(newHash, accountEntity.getCode());
        logout();
    }

    /**
     * 生成验证码
     *
     * @param req  请求
     * @param res  响应
     */
    public void getVerifyImage(HttpServletRequest req, HttpServletResponse res) {
        String code;
        try {
            HttpSession session = req.getSession();
            code = VerifyCodeUtil.outputVerifyImage(125, 50, res.getOutputStream(), 4, false);
            session.removeAttribute(GlobalConst.IMAGE_CODE);
            session.setAttribute(GlobalConst.IMAGE_CODE, code);
            res.flushBuffer();
        }catch (Exception e){
            log.error("图片验证码生成失败", e);
            throw new CustomException("图片验证码生成失败");
        }
    }

    /**
     * 校验图片验证码
     *
     * @param checkCode 校验验证码
     * @param session   session
     */
    public void checkImage(String checkCode, HttpSession session) {
        String sessionCode = (String) session.getAttribute(GlobalConst.IMAGE_CODE);
        // 当验证码为空或不匹配时抛错；匹配时通过
        if (StringUtils.isBlank(sessionCode) || !sessionCode.equalsIgnoreCase(checkCode)) {
            throw new CustomException("验证码输入错误");
        }
    }

}
