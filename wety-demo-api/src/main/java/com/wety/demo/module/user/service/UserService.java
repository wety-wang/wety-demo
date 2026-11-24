package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.DeptEntity;
import com.wety.demo.common.entity.DeptEntityRepository;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.user.vo.AccountDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 当前账号与用户下拉选项服务。
 *
 * @author wety
 */
@Service
public class UserService {

    @Resource
    private PermissionService permissionService;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private DeptEntityRepository deptEntityRepository;

    /**
     * 查询当前登录账号详情及其权限编码列表。
     *
     * @return 当前账号详情 VO
     */
    public AccountDetailVO queryAccount() {
        AccountEntity accountEntity = AppThreadLocal.getInstance().getCurrentAccount();
        AccountDetailVO accountDetailVO = new AccountDetailVO(accountEntity);
        if (StringUtils.isNotBlank(accountEntity.getPrimaryDeptCode())) {
            deptEntityRepository.findByCodeAndDeleted(accountEntity.getPrimaryDeptCode(), GlobalConst.NOT)
                    .map(DeptEntity::getName)
                    .ifPresent(accountDetailVO::setPrimaryDeptName);
        }
        accountDetailVO.setPermissionCodes(permissionService.listAccountPermission());
        return accountDetailVO;
    }

    /**
     * 查询所有未删除账号（供操作日志等下拉选择使用）。
     *
     * @return 未删除账号列表
     */
    public List<CodeNameDTO> listAll() {
        return listAccount(null);
    }

    /**
     * 查询所有未删除用户（供角色关联用户弹窗下拉使用）。
     *
     * @return 未删除用户列表
     */
    public List<CodeNameDTO> listUser() {
        return listAccount(null);
    }

    /**
     * 按类型查询未删除账号；accountType 为空时返回全部未删除账号。
     *
     * @param accountType 账号类型（可选）
     * @return 账号编码与姓名列表
     */
    public List<CodeNameDTO> listAccount(String accountType) {
        List<AccountEntity> accountEntityList = accountEntityRepository.findAllByDeleted(GlobalConst.NOT);
        return accountEntityList.stream()
                .filter(account -> accountType == null || accountType.equals(account.getType()))
                .map(account -> {
                    CodeNameDTO codeNameDTO = new CodeNameDTO();
                    codeNameDTO.setCode(account.getCode());
                    codeNameDTO.setName(account.getUserName());
                    return codeNameDTO;
                })
                .collect(Collectors.toList());
    }
}
