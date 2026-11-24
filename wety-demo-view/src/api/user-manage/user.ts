import type { UniversalCodeDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'
import { encryptSM2 } from '~/utils/crypto'

export interface AccountInfoVO {
  code: string
  loginName: string
  userName: string
  email?: string
  phoneNumber?: string
  type?: string
  createTime?: string
}

export interface UserAddDTO {
  userName: string
  loginName: string
  password: string
  email?: string
  phoneNumber?: string
  type?: string
}

export interface UserUpdateDTO {
  code: string
  userName?: string
  email?: string
  phoneNumber?: string
  type?: string
}

export async function listUserPageable(params: PageQuery) {
  return usePost<PageResult<AccountInfoVO>>('/bac/userManage/user/listPageable', params)
}

export async function addUser(data: UserAddDTO) {
  return usePost<string>('/bac/userManage/user/add', { ...data, password: encryptSM2(data.password) })
}

export async function queryUser(data: UniversalCodeDTO) {
  return usePost<AccountInfoVO>('/bac/userManage/user/query', data)
}

export async function updateUser(data: UserUpdateDTO) {
  return usePost<string>('/bac/userManage/user/update', data)
}

export async function deleteUser(data: UniversalCodeDTO) {
  return usePost<string>('/bac/userManage/user/delete', data)
}
