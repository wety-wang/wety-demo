package com.wety.demo.module.user.service;

import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.user.constant.AccountTypeConst;
import com.wety.demo.module.user.vo.AccountDetailVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wety
 */
@Service
public class UserService {

    @Resource
    private PermissionService permissionService;

    @Resource
    private AccountEntityRepository accountEntityRepository;

    public AccountDetailVO queryAccount() {
        AccountEntity accountEntity = AppThreadLocal.getInstance().getCurrentAccount();
        AccountDetailVO accountDetailVO = new AccountDetailVO(accountEntity);
        accountDetailVO.setPermissionCodes(permissionService.listAccountPermission());
        return accountDetailVO;
    }

    /**
     * 查询所有管理员用户
     *
     * @return
     */
    public List<CodeNameDTO> listAll() {
        return listAccount(null);
    }

    /**
     * 查询所有普通用户
     *
     * @return
     */
    public List<CodeNameDTO> listUser() {
        return listAccount(AccountTypeConst.NORMAL);
    }

    /**
     * 查询指定类型人员
     *
     * @param accountType
     * @return
     */
    public List<CodeNameDTO> listAccount(String accountType) {
        List<AccountEntity> accountEntityList;
        if(accountType == null) {
            accountEntityList = accountEntityRepository.findAll();
        }else{
            accountEntityList = accountEntityRepository.findAllByType(accountType);
        }
        return accountEntityList.stream()
                .map(account -> {
                    CodeNameDTO codeNameDTO = new CodeNameDTO();
                    codeNameDTO.setCode(account.getCode());
                    codeNameDTO.setName(account.getUserName());
                    return codeNameDTO;
                })
                .collect(Collectors.toList());
    }
}
