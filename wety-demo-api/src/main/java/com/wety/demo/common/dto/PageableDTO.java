package com.wety.demo.common.dto;

import com.wety.demo.common.exception.CustomException;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 接受分页参数的DTO，具体业务DTO可以继承该类，不必重复声明分页参数
 *
 * @author wety
 */
public class PageableDTO {

    @ApiModelProperty(value = "每页多少条", required = true, example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "查询第几页，从1开始", required = true, example = "1")
    private Integer pageNumber;

    @ApiModelProperty(value = "排序字段", example = "createTime")
    private String column;

    @ApiModelProperty(value = "排序方式", example = "desc")
    private String order;

    private static final String MISSING_PAGE_NUMBER_MESSAGE = "缺少参数[pageNumber]";
    private static final String MISSING_PAGE_SIZE_MESSAGE = "缺少参数[pageSize]";

    public PageableDTO() {
    }

    public PageableDTO(PageableDTO dto) {
        this.pageSize = dto.getPageSize();
        this.pageNumber = dto.getPageNumber();
        this.column = dto.getColumn();
        this.order = dto.getOrder();
    }

    /**
     * 每页多少条
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public Integer getPageSize() {
        if (null == pageSize) {
            throw new CustomException(MISSING_PAGE_SIZE_MESSAGE);
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 请求页码数，0表示第一页
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public Integer getPageNumber() {
        if (null == pageNumber) {
            throw new CustomException(MISSING_PAGE_NUMBER_MESSAGE);
        }
        return pageNumber;
    }

    /**
     * 实际页码数，从0开始
     */
    @ApiModelProperty(hidden = true)
    public Integer getRealPageNumber() {
        return getPageNumber() - 1;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    @ApiModelProperty(hidden = true)
    public Pageable getPageable() {
        return getPageable((Sort.Order) null);
    }

    /**
     * 如果没有设置排序，则使用默认排序字段和排序方式
     * @param defaultOrders
     * @return
     */
    @ApiModelProperty(hidden = true)
    public Pageable getPageableWithDefault(Sort.Order... defaultOrders) {
        if (null == pageNumber) {
            throw new CustomException(MISSING_PAGE_NUMBER_MESSAGE);
        }
        if (null == pageSize) {
            throw new CustomException(MISSING_PAGE_SIZE_MESSAGE);
        }
        List<Sort.Order> orderList = new ArrayList<>();
        if(StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(order)){
            orderList.add(new Sort.Order(Sort.Direction.fromString(order), column));
        }else if(defaultOrders!=null){
            for (Sort.Order sortOrder : defaultOrders) {
                if(sortOrder != null){
                    orderList.add(sortOrder);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(orderList)) {
            return PageRequest.of(getRealPageNumber(), pageSize, Sort.by(orderList));
        } else {
            return PageRequest.of(getRealPageNumber(), pageSize);
        }
    }

    /**
     * example: queryDTO.getPageable(new Order(Direction.DESC, "id"));
     *
     * @param orders
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public Pageable getPageable(Sort.Order... orders) {
        if (null == pageNumber) {
            throw new CustomException(MISSING_PAGE_NUMBER_MESSAGE);
        }
        if (null == pageSize) {
            throw new CustomException(MISSING_PAGE_SIZE_MESSAGE);
        }
        List<Sort.Order> orderList = new ArrayList<>();
        if (StringUtils.isNotEmpty(column) && StringUtils.isNotEmpty(order)) {
            orderList.add(new Sort.Order(Sort.Direction.fromString(order), column));
        }
        if(orders!=null){
            for (Sort.Order sortOrder : orders) {
                if(sortOrder != null){
                    orderList.add(sortOrder);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(orderList)) {
            return PageRequest.of(getRealPageNumber(), pageSize, Sort.by(orderList));
        } else {
            return PageRequest.of(getRealPageNumber(), pageSize);
        }
    }

    @Override
    public String toString() {
        return "PageableDTO{" + "pageSize=" + pageSize + ", pageNumber=" + pageNumber + '}';
    }
}
