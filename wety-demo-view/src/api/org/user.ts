import type { UniversalCodeDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'
import { encryptSM2 } from '~/utils/crypto'

/**
 * 用户分页查询 DTO
 */
export interface OrgUserPageQueryDTO extends PageQuery {
  /** 部门编码筛选（包含子孙部门） */
  deptCode?: string
  /** 姓名模糊搜索 */
  userName?: string
  /** 登录名模糊搜索 */
  loginName?: string
  /** 手机号模糊搜索 */
  phoneNumber?: string
  /** 邮箱模糊搜索 */
  email?: string
  /** 状态筛选：0 启用，1 禁用 */
  disabled?: number
}

/**
 * 用户信息 VO
 */
export interface OrgUserInfoVO {
  /** 账号业务编码 */
  code: string
  /** 登录名 */
  loginName: string
  /** 用户姓名 */
  userName: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phoneNumber?: string
  /** 是否禁用：0 启用，1 禁用 */
  disabled: number
  /** 是否内置超管：1 是，0 否 */
  superAdmin: number
  /** 主部门编码 */
  primaryDeptCode?: string
  /** 主部门名称 */
  primaryDeptName?: string
  /** 岗位编码列表 */
  positionCodes?: string[]
  /** 岗位名称列表 */
  positionNames?: string[]
  /** 角色编码列表 */
  roleCodes?: string[]
  /** 角色名称列表 */
  roleNames?: string[]
  /** 创建时间 */
  createTime?: string
}

/**
 * 新增用户 DTO
 */
export interface OrgUserAddDTO {
  /** 登录名 */
  loginName: string
  /** 姓名 */
  userName: string
  /** 密码（调用接口前自动进行 SM2 加密） */
  password: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phoneNumber?: string
  /** 主部门编码 */
  primaryDeptCode: string
  /** 关联岗位编码列表 */
  positionCodes?: string[]
  /** 关联角色编码列表 */
  roleCodes?: string[]
  /** 初始状态：0 启用，1 禁用 */
  disabled?: number
}

/**
 * 修改用户 DTO
 */
export interface OrgUserUpdateDTO {
  /** 账号业务编码 */
  code: string
  /** 姓名 */
  userName: string
  /** 邮箱 */
  email?: string
  /** 手机号 */
  phoneNumber?: string
  /** 主部门编码 */
  primaryDeptCode: string
  /** 关联岗位编码列表 */
  positionCodes?: string[]
  /** 关联角色编码列表 */
  roleCodes?: string[]
}

/**
 * 启停用户 DTO
 */
export interface OrgUserStatusUpdateDTO {
  /** 账号业务编码 */
  code: string
  /** 目标状态：0 启用，1 禁用 */
  disabled: number
}

/**
 * 重置密码 DTO
 */
export interface OrgUserResetPasswordDTO {
  /** 账号业务编码 */
  code: string
  /** 新密码（调用接口前自动进行 SM2 加密） */
  newPassword: string
}

/**
 * 分页查询用户
 */
export async function listUserPageable(params: OrgUserPageQueryDTO) {
  return usePost<PageResult<OrgUserInfoVO>>('/bac/org/user/listPageable', params)
}

/**
 * 查询用户详情
 */
export async function queryUser(data: UniversalCodeDTO) {
  return usePost<OrgUserInfoVO>('/bac/org/user/query', data)
}

/**
 * 新增用户（密码经 SM2 加密传输）
 */
export async function addUser(data: OrgUserAddDTO) {
  return usePost<string>('/bac/org/user/add', {
    ...data,
    password: encryptSM2(data.password),
  })
}

/**
 * 修改用户
 */
export async function updateUser(data: OrgUserUpdateDTO) {
  return usePost<string>('/bac/org/user/update', data)
}

/**
 * 删除用户
 */
export async function deleteUser(data: UniversalCodeDTO) {
  return usePost<string>('/bac/org/user/delete', data)
}

/**
 * 启停用户
 */
export async function updateUserStatus(data: OrgUserStatusUpdateDTO) {
  return usePost<string>('/bac/org/user/updateStatus', data)
}

/**
 * 重置用户密码（新密码经 SM2 加密传输）
 */
export async function resetUserPassword(data: OrgUserResetPasswordDTO) {
  return usePost<string>('/bac/org/user/resetPassword', {
    code: data.code,
    newPassword: encryptSM2(data.newPassword),
  })
}
