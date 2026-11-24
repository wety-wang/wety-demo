import type { CodeNameDTO } from '~@/api/constant'
import type { PageQuery, PageResult } from '~@/utils/pagination'

export interface OperationLogInfoVO {
  modelCode?: string
  modelName?: string
  path?: string
  accountCode?: string
  accountLoginName?: string
  accountName?: string
  deptCode?: string
  deptName?: string
  clientIp?: string
  operationTime?: string
  operationType?: string
  result?: string
  description?: string
  durationMs?: number
  loginLogId?: number
  loginLogCode?: string
}

export interface OperationLogPageQuery extends PageQuery {
  // 按用户编码多选筛选
  accountCodes?: string[]
  result?: string
  clientIp?: string
  path?: string
  // 支持多选模块编码
  modelCodes?: string[]
  // 操作类型多选（create/update/delete/query）
  operationTypes?: string[]
  operationTime?: string[]
  module?: string
  resource?: string
  action?: string
}

export async function listOperationLogPageable(params: OperationLogPageQuery) {
  return usePost<PageResult<OperationLogInfoVO>>('/bac/log/operationLog/listPageable', params)
}

// 权限树结构
export interface PermissionHolder {
  code: string
  name: string
  parentCode?: string
  leafNode?: string
  showTreeNode?: string
  children?: PermissionHolder[]
}

// 查询模块树
export async function queryModuleTree() {
  return useGet<PermissionHolder[]>('/bac/log/operationLog/queryModuleTree')
}

// 查询所有用户列表
export async function listAllUser() {
  return useGet<CodeNameDTO[]>('/bac/log/operationLog/listAllUser')
}
