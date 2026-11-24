import type { CodeNameDTO, UniversalCodeDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'

/**
 * 岗位分页查询 DTO
 */
export interface PositionPageQueryDTO extends PageQuery {
  /** 岗位名称模糊搜索 */
  name?: string
  /** 岗位标识编码模糊搜索 */
  postCode?: string
  /** 状态：1 启用，0 停用 */
  status?: number
}

/**
 * 岗位列表 VO
 */
export interface PositionInfoVO {
  /** 岗位业务编码 */
  code: string
  /** 岗位标识编码 */
  postCode: string
  /** 岗位名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 状态：1 启用，0 停用 */
  status: number
  /** 备注 */
  remark?: string
  /** 创建时间 */
  createTime?: string
}

/**
 * 新增岗位 DTO
 */
export interface PositionAddDTO {
  /** 岗位标识编码 */
  postCode: string
  /** 岗位名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 状态：1 启用，0 停用 */
  status?: number
  /** 备注 */
  remark?: string
}

/**
 * 修改岗位 DTO
 */
export interface PositionUpdateDTO {
  /** 岗位业务编码 */
  code: string
  /** 岗位标识编码 */
  postCode: string
  /** 岗位名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 状态：1 启用，0 停用 */
  status?: number
  /** 备注 */
  remark?: string
}

/**
 * 启停岗位 DTO
 */
export interface PositionStatusUpdateDTO {
  /** 岗位业务编码 */
  code: string
  /** 目标状态：1 启用，0 停用 */
  status: number
}

/**
 * 分页查询岗位
 */
export async function listPositionPageable(params: PositionPageQueryDTO) {
  return usePost<PageResult<PositionInfoVO>>('/bac/org/position/listPageable', params)
}

/**
 * 查询全部启用岗位（下拉用）
 */
export async function listAllPosition() {
  return useGet<CodeNameDTO[]>('/bac/org/position/listAll')
}

/**
 * 新增岗位
 */
export async function addPosition(data: PositionAddDTO) {
  return usePost<string>('/bac/org/position/add', data)
}

/**
 * 修改岗位
 */
export async function updatePosition(data: PositionUpdateDTO) {
  return usePost<string>('/bac/org/position/update', data)
}

/**
 * 删除岗位
 */
export async function deletePosition(data: UniversalCodeDTO) {
  return usePost<string>('/bac/org/position/delete', data)
}

/**
 * 启停岗位
 */
export async function updatePositionStatus(data: PositionStatusUpdateDTO) {
  return usePost<string>('/bac/org/position/updateStatus', data)
}
