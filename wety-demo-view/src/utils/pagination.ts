// 通用分页工具（全局复用）

// 前端分页查询参数
export interface PageQuery {
  pageNumber?: number
  pageSize?: number
}

// 后端分页返回结构
export interface PageResult<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
  empty: boolean
  // 可选：排序与分页附加信息
  sort?: unknown
  pageable?: unknown
}
