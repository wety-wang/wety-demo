import type { CodeNameDTO, UniversalCodeDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'

// 角色列表返回结构
export interface RoleInfoVO {
  code: string
  name: string
  // 创建时间
  createTime?: string
}

// 角色新增/修改入参
export interface RoleAddDTO { name: string }
export interface RoleUpdateDTO { code: string, name: string }

// 角色分页查询入参
export interface RolePageQuery extends PageQuery { name?: string }

// 权限树结构
export interface PermissionHolder {
  code: string
  name: string
  parentCode?: string
  leafNode?: string
  showTreeNode?: string
  children?: PermissionHolder[]
}
// 角色权限查询返回结构
export interface RolePermissionVO {
  codes: string[]
  hierarchicalList: PermissionHolder[]
}

// 角色权限更新入参
export interface RolePermissionUpdateDTO {
  roleCode: string
  permissionCodeList: string[]
}

// 角色-用户分页查询入参与返回结构
export interface RoleUserPageQuery extends PageQuery {
  userName?: string
  roleCode: string
}
export interface RoleUserInfoVO {
  roleCode: string
  roleName?: string
  accountCode: string
  accountName?: string
}

// 角色-用户新增/删除入参
export interface RoleUserAddDTO {
  roleCode: string
  accountCodes: string[]
}
export interface RoleUserDeleteDTO {
  roleCode: string
  accountCode: string
}

// 列表查询
export async function listRolePageable(params: RolePageQuery) {
  return usePost<PageResult<RoleInfoVO>>('/bac/userManage/role/listPageable', params)
}

// 新增/修改/删除
export async function addRole(data: RoleAddDTO) {
  return usePost<string>('/bac/userManage/role/add', data)
}
export async function updateRole(data: RoleUpdateDTO) {
  return usePost<string>('/bac/userManage/role/update', data)
}
export async function deleteRole(data: UniversalCodeDTO) {
  return usePost<string>('/bac/userManage/role/delete', data)
}

// 权限相关
export async function queryRolePermission(data: UniversalCodeDTO) {
  return usePost<RolePermissionVO>('/bac/userManage/role/queryPermission', data)
}
export async function updateRolePermission(data: RolePermissionUpdateDTO) {
  return usePost<string>('/bac/userManage/role/updatePermission', data)
}

// 角色用户关联相关
export async function listRoleUserPageable(params: RoleUserPageQuery) {
  return usePost<PageResult<RoleUserInfoVO>>('/bac/userManage/role/listRoleUserPageable', params)
}
export async function listAllUser() {
  return useGet<CodeNameDTO[]>('/bac/userManage/role/listAllUser')
}
export async function addRoleUser(data: RoleUserAddDTO) {
  return usePost<string>('/bac/userManage/role/addRoleUser', data)
}
export async function deleteRoleUser(data: RoleUserDeleteDTO) {
  return usePost<string>('/bac/userManage/role/deleteRoleUser', data)
}
