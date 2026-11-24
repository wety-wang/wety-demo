import type { UniversalCodeDTO } from '~@/api/constant'

/**
 * 部门树查询条件 DTO
 */
export interface DeptTreeQueryDTO {
  /** 部门名称模糊搜索 */
  name?: string
  /** 状态筛选：1 启用，0 停用 */
  status?: number
}

/**
 * 部门树节点 VO
 */
export interface DeptTreeVO {
  /** 部门业务编码 */
  code: string
  /** 父部门编码（根为 "0"） */
  parentCode: string
  /** 祖级路径 */
  ancestors?: string
  /** 部门名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 负责人账号编码 */
  leaderAccountCode?: string
  /** 负责人姓名 */
  leaderAccountName?: string
  /** 联系电话 */
  phone?: string
  /** 联系邮箱 */
  email?: string
  /** 状态：1 启用，0 停用 */
  status: number
  /** 创建时间 */
  createTime?: string
  /** 子部门列表 */
  children?: DeptTreeVO[]
}

/**
 * 新增部门 DTO
 */
export interface DeptAddDTO {
  /** 父部门编码 */
  parentCode: string
  /** 部门名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 部门负责人账号编码 */
  leaderAccountCode?: string
  /** 联系电话 */
  phone?: string
  /** 联系邮箱 */
  email?: string
  /** 状态：1 启用，0 停用 */
  status?: number
}

/**
 * 修改部门 DTO
 */
export interface DeptUpdateDTO {
  /** 部门业务编码 */
  code: string
  /** 父部门编码 */
  parentCode: string
  /** 部门名称 */
  name: string
  /** 排序号 */
  sortOrder?: number
  /** 部门负责人账号编码 */
  leaderAccountCode?: string
  /** 联系电话 */
  phone?: string
  /** 联系邮箱 */
  email?: string
  /** 状态：1 启用，0 停用 */
  status?: number
}

/**
 * 启停部门 DTO
 */
export interface DeptStatusUpdateDTO {
  /** 部门业务编码 */
  code: string
  /** 目标状态：1 启用，0 停用 */
  status: number
}

/**
 * 查询部门树
 */
export async function queryDeptTree(data: DeptTreeQueryDTO = {}) {
  return usePost<DeptTreeVO[]>('/bac/org/dept/tree', data)
}

/**
 * 新增部门
 */
export async function addDept(data: DeptAddDTO) {
  return usePost<string>('/bac/org/dept/add', data)
}

/**
 * 修改部门
 */
export async function updateDept(data: DeptUpdateDTO) {
  return usePost<string>('/bac/org/dept/update', data)
}

/**
 * 删除部门
 */
export async function deleteDept(data: UniversalCodeDTO) {
  return usePost<string>('/bac/org/dept/delete', data)
}

/**
 * 启停部门
 */
export async function updateDeptStatus(data: DeptStatusUpdateDTO) {
  return usePost<string>('/bac/org/dept/updateStatus', data)
}
