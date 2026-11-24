package com.wety.demo.module.organization.service;

import com.wety.demo.common.constant.GlobalConst;
import com.wety.demo.common.dto.CodeNameDTO;
import com.wety.demo.common.entity.AccountEntity;
import com.wety.demo.common.entity.AccountPositionLinkEntityRepository;
import com.wety.demo.common.entity.PositionEntity;
import com.wety.demo.common.entity.PositionEntityRepository;
import com.wety.demo.common.exception.CustomException;
import com.wety.demo.common.util.UUIDGenerator;
import com.wety.demo.core.misc.AppThreadLocal;
import com.wety.demo.module.organization.dto.PositionAddDTO;
import com.wety.demo.module.organization.dto.PositionPageQueryDTO;
import com.wety.demo.module.organization.dto.PositionStatusUpdateDTO;
import com.wety.demo.module.organization.dto.PositionUpdateDTO;
import com.wety.demo.module.organization.vo.PositionInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位管理服务。
 *
 * <p>负责岗位分页查询、下拉列表查询、新增、修改、软删除及启停管理。</p>
 */
@Service
@Transactional(readOnly = true)
public class PositionService {

    private static final Logger log = LoggerFactory.getLogger(PositionService.class);

    /** 启用状态。 */
    private static final Integer STATUS_ENABLE = GlobalConst.DATA_STATUS_ENABLE;

    /** 停用状态。 */
    private static final Integer STATUS_DISABLE = GlobalConst.DATA_STATUS_DISABLE;

    @Resource
    private PositionEntityRepository positionEntityRepository;

    @Resource
    private AccountPositionLinkEntityRepository accountPositionLinkEntityRepository;

    /**
     * 分页查询岗位。
     *
     * @param queryDTO 分页与筛选参数
     * @return 岗位分页结果
     */
    public Page<PositionInfoVO> listPageable(PositionPageQueryDTO queryDTO) {
        // 强制过滤软删除数据，防止外部篡改
        queryDTO.setDeleted(GlobalConst.NOT);
        Pageable pageable = queryDTO.getPageableWithDefault(
                new Sort.Order(Sort.Direction.ASC, "sortOrder"),
                new Sort.Order(Sort.Direction.ASC, "id")
        );
        return positionEntityRepository.findAll(queryDTO, pageable).map(PositionInfoVO::new);
    }

    /**
     * 查询全部启用岗位（下拉用，按 sortOrder 升序）。
     *
     * @return 启用岗位列表
     */
    public List<CodeNameDTO> listAll() {
        List<PositionEntity> positions = positionEntityRepository
                .findAllByStatusAndDeletedOrderBySortOrderAscIdAsc(STATUS_ENABLE, GlobalConst.NOT);
        return positions.stream()
                .map(position -> {
                    CodeNameDTO dto = new CodeNameDTO();
                    dto.setCode(position.getCode());
                    dto.setName(position.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 新增岗位。
     *
     * @param addDTO 新增岗位参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(PositionAddDTO addDTO) {
        if (addDTO == null || StringUtils.isBlank(addDTO.getPostCode())) {
            throw new CustomException("岗位编码不能为空");
        }
        if (StringUtils.isBlank(addDTO.getName())) {
            throw new CustomException("岗位名称不能为空");
        }
        String postCode = addDTO.getPostCode().trim();
        String name = addDTO.getName().trim();
        if (postCode.length() > 64) {
            throw new CustomException("岗位编码长度不能超过64");
        }
        if (name.length() > 100) {
            throw new CustomException("岗位名称长度不能超过100");
        }

        if (positionEntityRepository.existsByPostCodeAndDeleted(postCode, GlobalConst.NOT)) {
            throw new CustomException("岗位编码已存在");
        }
        if (positionEntityRepository.existsByNameAndDeleted(name, GlobalConst.NOT)) {
            throw new CustomException("岗位名称已存在");
        }

        Integer status = normalizeStatus(addDTO.getStatus(), STATUS_ENABLE);
        String currentUserCode = getCurrentAccountCode();
        Date now = new Date();

        PositionEntity entity = new PositionEntity();
        entity.setCode(UUIDGenerator.generatorUUID());
        entity.setPostCode(postCode);
        entity.setName(name);
        entity.setSortOrder(addDTO.getSortOrder() == null ? 0 : addDTO.getSortOrder());
        entity.setStatus(status);
        entity.setRemark(StringUtils.trimToNull(addDTO.getRemark()));
        entity.setCreateUser(currentUserCode);
        entity.setUpdateUser(currentUserCode);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setDeleted(GlobalConst.NOT);

        positionEntityRepository.save(entity);
        log.info("新增岗位成功，code: {}, postCode: {}, name: {}", entity.getCode(), postCode, name);
    }

    /**
     * 修改岗位。
     *
     * @param updateDTO 修改岗位参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(PositionUpdateDTO updateDTO) {
        if (updateDTO == null || StringUtils.isBlank(updateDTO.getCode())) {
            throw new CustomException("岗位业务编码不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getPostCode())) {
            throw new CustomException("岗位编码不能为空");
        }
        if (StringUtils.isBlank(updateDTO.getName())) {
            throw new CustomException("岗位名称不能为空");
        }
        String code = updateDTO.getCode().trim();
        String postCode = updateDTO.getPostCode().trim();
        String name = updateDTO.getName().trim();
        if (postCode.length() > 64) {
            throw new CustomException("岗位编码长度不能超过64");
        }
        if (name.length() > 100) {
            throw new CustomException("岗位名称长度不能超过100");
        }

        PositionEntity entity = getUndeletedPosition(code);

        if (positionEntityRepository.existsByPostCodeAndDeletedAndCodeNot(postCode, GlobalConst.NOT, code)) {
            throw new CustomException("岗位编码已存在");
        }
        if (positionEntityRepository.existsByNameAndDeletedAndCodeNot(name, GlobalConst.NOT, code)) {
            throw new CustomException("岗位名称已存在");
        }

        entity.setPostCode(postCode);
        entity.setName(name);
        if (updateDTO.getSortOrder() != null) {
            entity.setSortOrder(updateDTO.getSortOrder());
        }
        if (updateDTO.getStatus() != null) {
            entity.setStatus(normalizeStatus(updateDTO.getStatus(), entity.getStatus()));
        }
        entity.setRemark(StringUtils.trimToNull(updateDTO.getRemark()));
        entity.setUpdateUser(getCurrentAccountCode());
        entity.setUpdateTime(new Date());

        positionEntityRepository.save(entity);
        log.info("修改岗位成功，code: {}, postCode: {}, name: {}", code, postCode, name);
    }

    /**
     * 软删除岗位。
     *
     * @param code 岗位业务编码
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException("岗位业务编码不能为空");
        }
        PositionEntity entity = getUndeletedPosition(code.trim());
        if (accountPositionLinkEntityRepository.existsByPositionCodeAndDeleted(entity.getCode(), GlobalConst.NOT)) {
            throw new CustomException("该岗位已被用户引用，无法删除");
        }

        entity.setDeleted(GlobalConst.YESI);
        entity.setUpdateUser(getCurrentAccountCode());
        entity.setUpdateTime(new Date());
        positionEntityRepository.save(entity);
        log.info("删除岗位成功，code: {}", entity.getCode());
    }

    /**
     * 启停岗位。
     *
     * @param statusUpdateDTO 启停参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(PositionStatusUpdateDTO statusUpdateDTO) {
        if (statusUpdateDTO == null || StringUtils.isBlank(statusUpdateDTO.getCode())) {
            throw new CustomException("岗位业务编码不能为空");
        }
        Integer targetStatus = normalizeStatus(statusUpdateDTO.getStatus(), null);
        PositionEntity entity = getUndeletedPosition(statusUpdateDTO.getCode().trim());

        entity.setStatus(targetStatus);
        entity.setUpdateUser(getCurrentAccountCode());
        entity.setUpdateTime(new Date());
        positionEntityRepository.save(entity);
        log.info("更新岗位状态成功，code: {}, status: {}", entity.getCode(), targetStatus);
    }

    /**
     * 查询未删除岗位，不存在时抛出业务异常。
     */
    private PositionEntity getUndeletedPosition(String code) {
        return positionEntityRepository.findByCodeAndDeleted(code, GlobalConst.NOT)
                .orElseThrow(() -> new CustomException("岗位不存在或已删除"));
    }

    /**
     * 校验并规范化状态值（仅允许 0 或 1）。
     */
    private Integer normalizeStatus(Integer status, Integer defaultValue) {
        if (status == null) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new CustomException("状态不能为空");
        }
        if (!STATUS_ENABLE.equals(status) && !STATUS_DISABLE.equals(status)) {
            throw new CustomException("非法的状态值，仅支持 0（停用）或 1（启用）");
        }
        return status;
    }

    /**
     * 获取当前登录账号编码。
     */
    private String getCurrentAccountCode() {
        AccountEntity currentAccount = AppThreadLocal.getInstance().getCurrentAccount();
        return currentAccount != null ? currentAccount.getCode() : null;
    }
}
