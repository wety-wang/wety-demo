import type { PageQuery, PageResult } from '~@/utils/pagination'

export interface LoginLogInfoVO {
  id?: number
  accountCode?: string
  accountLoginName?: string
  accountName?: string
  operationTime?: string
  result?: string
  description?: string
  lastOperationTime?: string
  clientIp?: string
  logoutTime?: string
  userAgent?: string
}

export interface LoginLogPageQuery extends PageQuery {
  accountLoginName?: string
  accountName?: string
  result?: string
  clientIp?: string
}

export async function listLoginLogPageable(params: LoginLogPageQuery) {
  return usePost<PageResult<LoginLogInfoVO>>('/bac/log/loginLog/listPageable', params)
}
