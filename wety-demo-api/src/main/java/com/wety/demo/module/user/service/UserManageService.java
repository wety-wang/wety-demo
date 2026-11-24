package com.wety.demo.module.user.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountEntityRepository;
import com.wety.demo.common.entity.AccountRoleLinkEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.SM2Utils;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.module.user.dto.UserAddDTO;
import com.wety.demo.module.user.dto.UserPageQueryDTO;
import com.wety.demo.module.user.dto.UserUpdateDTO;
import com.wety.demo.module.user.vo.AccountInfoVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Optional;

/**
 * @author wety
 */
@Service
public class UserManageService {

    @Resource
    private AccountEntityRepository accountEntityRepository;

    @Resource
    private AccountRoleLinkEntityRepository accountRoleLinkEntityRepository;

    /**
     * 列表页
     *
     * @param pageDTO
     * @return
     */
    public Page<AccountInfoVO> listPageable(UserPageQueryDTO queryDTO) {
        Pageable pageable = queryDTO.getPageable(new Sort.Order(Sort.Direction.ASC, GlobalConst.SORT_CREATE_TIME));
        return accountEntityRepository.findAll(queryDTO, pageable).map(AccountInfoVO::new);
    }

    /**
     * 查询用户信息
     *
     * @param code 用户编码
     * @return PageVO 用户信息VO
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public AccountInfoVO query(String code) {
        AccountEntity accountEntity = getAccountEntity(code);
        return new AccountInfoVO(accountEntity);
    }

    private AccountEntity getAccountEntity(String code) {
        Optional<AccountEntity> selectEntity = accountEntityRepository.findByCode(code);
        return selectEntity.orElseThrow(() -> new CustomException("用户不存在"));
    }

    /**
     * 新增
     *
     * @param saveDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void add(UserAddDTO saveDTO) {
        // 判断登陆名是否已存在
        boolean exists = accountEntityRepository.existsByLoginName(saveDTO.getLoginName());
        if (exists) {
            throw new CustomException("该登陆名已存在");
        }
        AccountEntity saveEntity = new AccountEntity();
        saveEntity.setCode(UUIDGenerator.generatorUUID());
        saveEntity.setLoginName(saveDTO.getLoginName());
        saveEntity.setUserName(saveDTO.getUserName());
        String decryptedPassword = SM2Utils.decrypt(saveDTO.getPassword());
        saveEntity.setPassword(new BCryptPasswordEncoder().encode(decryptedPassword));
        saveEntity.setDisabled(GlobalConst.NOT);
        saveEntity.setEmail(saveDTO.getEmail());
        saveEntity.setPhoneNumber(saveDTO.getPhoneNumber());
        saveEntity.setType(saveDTO.getType());
        saveEntity.setCreateTime(new Date());
        saveEntity.setUpdateTime(new Date());
        accountEntityRepository.save(saveEntity);
    }

    /**
     * 修改
     *
     * @param saveDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void update(UserUpdateDTO updateDTO) {
        AccountEntity selectEntity = getAccountEntity(updateDTO.getCode());
        selectEntity.setUserName(updateDTO.getUserName());
        selectEntity.setEmail(updateDTO.getEmail());
        selectEntity.setPhoneNumber(updateDTO.getPhoneNumber());
        selectEntity.setType(updateDTO.getType());
        selectEntity.setUpdateTime(new Date());
        accountEntityRepository.save(selectEntity);
    }

    /**
     * 删除
     *
     * @param codeDTO
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void delete(String code) {
        Optional<AccountEntity> accountEntityOptional = accountEntityRepository.findByCode(code);
        accountEntityOptional.orElseThrow(() -> new CustomException(GlobalConst.DELETE_ERROR));
        accountEntityRepository.deleteByCode(code);
        accountRoleLinkEntityRepository.deleteByAccountCode(code);
    }
}
