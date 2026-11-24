// 后端返回的账户信息（AccountDetailVO），前端在 store 中做一次映射
export interface AccountDTO {
  code: string
  loginName: string
  userName: string
  email?: string
  phoneNumber?: string
  type?: string
  superAdmin?: number
  primaryDeptCode?: string
  primaryDeptName?: string
  roleNames?: string
  roleCodes?: string[]
  permissionCodes?: (string | number)[]
}

export function getUserInfoApi() {
  return usePost<AccountDTO>('/bac/common/queryAccount', undefined, {
    loading: true,
  })
}
