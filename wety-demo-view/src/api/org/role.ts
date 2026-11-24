import type { CodeNameDTO, UniversalCodeDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'

/**
 * 数据权限范围选项
 */
export const DATA_SCOPE_OPTIONS = [
  { value: 1, label: '全部数据权限', color: 'red' },
  { value: 2, label: '自定义部门数据权限', color: 'purple' },
  { value: 3, label: '本部门数据权限', color: 'blue' },
  { value: 4, label: '本部门及以下数据权限', color: 'cyan' },
  { value: 5, label: '仅本人数据权限', color: 'default' },
] as const

export const DATA_SCOPE_MAP: Record<number, { label: string, color: string }> = Object.fromEntries(
  DATA_SCOPE_OPTIONS.map(item => [item.value, { label: item.label, color: item.color }]),
)

/**
 * 角色列表 VO
 */
export interface RoleInfoVO {
  /** 角色业务编码 */
  code: string
  /** 角色名称 */
  name: string
  /** 数据权限范围：1 全部、2 自定义部门、3 本部门、4 本部门及以下、5 仅本人 */
  dataScope: number
  /** 数据权限范围中文名称 */
  dataScopeName?: string
  /** 创建时间 */
  createTime?: string
}

/**
 * 新增角色 DTO
 */
export interface RoleAddDTO {
  /** 角色名称 */
  name: string
  /** 初始数据权限范围（默认 5） */
  dataScope?: number
}

/**
 * 修改角色 DTO
 */
export interface RoleUpdateDTO {
  /** 角色业务编码 */
  code: string
  /** 角色名称 */
  name: string
}

/**
 * 角色分页查询 DTO
 */
export interface RolePageQuery extends PageQuery {
  /** 角色名称模糊搜索 */
  name?: string
}

/**
 * 权限树节点结构
 */
export interface PermissionHolder {
  code: string
  name: string
  parentCode?: string
  leafNode?: string
  showTreeNode?: string
  children?: PermissionHolder[]
}

/**
 * 角色菜单权限 VO
 */
export interface RolePermissionVO {
  codes: string[]
  hierarchicalList: PermissionHolder[]
}

/**
 * 角色菜单权限更新 DTO
 */
export interface RolePermissionUpdateDTO {
  roleCode: string
  permissionCodeList: string[]
}

/**
 * 角色数据权限 VO
 */
export interface RoleDataScopeVO {
  /** 角色业务编码 */
  roleCode: string
  /** 当前数据范围（1~5） */
  dataScope: number
  /** 自定义部门编码列表（仅 dataScope == 2 时有值） */
  deptCodes?: string[]
}

/**
 * 修改角色数据权限 DTO
 */
export interface RoleDataScopeUpdateDTO {
  /** 角色业务编码 */
  roleCode: string
  /** 目标数据范围（1~5） */
  dataScope: number
  /** 自定义部门编码列表（dataScope == 2 时必填） */
  deptCodes?: string[]
}

/**
 * 角色-用户分页查询 DTO
 */
export interface RoleUserPageQuery extends PageQuery {
  userName?: string
  roleCode: string
}

/**
 * 角色-用户 VO
 */
export interface RoleUserInfoVO {
  roleCode: string
  roleName?: string
  accountCode: string
  accountName?: string
}

/**
 * 角色-用户新增/删除 DTO
 */
export interface RoleUserAddDTO {
  roleCode: string
  accountCodes: string[]
}

export interface RoleUserDeleteDTO {
  roleCode: string
  accountCode: string
}

/**
 * 分页查询角色
 */
export async function listRolePageable(params: RolePageQuery) {
  return usePost<PageResult<RoleInfoVO>>('/bac/org/role/listPageable', params)
}

/**
 * 查询全部有效角色（下拉用）
 */
export async function listAllRole() {
  return useGet<CodeNameDTO[]>('/bac/org/role/listAll')
}

/**
 * 新增角色
 */
export async function addRole(data: RoleAddDTO) {
  return usePost<string>('/bac/org/role/add', data)
}

/**
 * 修改角色
 */
export async function updateRole(data: RoleUpdateDTO) {
  return usePost<string>('/bac/org/role/update', data)
}

/**
 * 删除角色
 */
export async function deleteRole(data: UniversalCodeDTO) {
  return usePost<string>('/bac/org/role/delete', data)
}

/**
 * 查询菜单权限
 */
export async function queryRolePermission(data: UniversalCodeDTO) {
  return usePost<RolePermissionVO>('/bac/org/role/queryPermission', data)
}

/**
 * 修改菜单权限
 */
export async function updateRolePermission(data: RolePermissionUpdateDTO) {
  return usePost<string>('/bac/org/role/updatePermission', data)
}

/**
 * 查询角色数据权限
 */
export async function queryRoleDataScope(data: UniversalCodeDTO) {
  return usePost<RoleDataScopeVO>('/bac/org/role/queryDataScope', data)
}

/**
 * 修改角色数据权限
 */
export async function updateRoleDataScope(data: RoleDataScopeUpdateDTO) {
  return usePost<string>('/bac/org/role/updateDataScope', data)
}

/**
 * 分页查询角色关联用户
 */
export async function listRoleUserPageable(params: RoleUserPageQuery) {
  return usePost<PageResult<RoleUserInfoVO>>('/bac/org/role/listRoleUserPageable', params)
}

/**
 * 查询全部未删除用户（下拉/选择用）
 */
export async function listAllUser() {
  return useGet<CodeNameDTO[]>('/bac/org/role/listAllUser')
}

/**
 * 新增角色关联用户
 */
export async function addRoleUser(data: RoleUserAddDTO) {
  return usePost<string>('/bac/org/role/addRoleUser', data)
}

/**
 * 删除角色关联用户
 */
export async function deleteRoleUser(data: RoleUserDeleteDTO) {
  return usePost<string>('/bac/org/role/deleteRoleUser', data)
}
