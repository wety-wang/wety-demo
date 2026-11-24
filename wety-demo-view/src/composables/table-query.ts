import type { PaginationProps } from 'ant-design-vue'
import type { TableRowSelection } from 'ant-design-vue/es/table/interface'
import { assign } from 'lodash'

/**
 * 表格分页扩展类型
 */
export interface TablePaginationProps extends PaginationProps {
  /**
   * 当前页码（从1开始）
   * 用于代替PaginationProps中的current与后端通信
   */
  pageNumber?: number
  /**
   * 排序字段
   */
  column: string
  /**
   * 排序方式
   */
  order: string
}

/**
 * 表格选择框扩展类型
 */
export interface TableRowSelectionsProps extends TableRowSelection {
  /**
   * 选择行
   */
  selectedRows: any[]
  /**
   * 选择行key
   */
  selectedRowKeys: any[]
}

/**
 * afterQuery 回调期望返回的通用结构
 * 为了适配不同后端分页返回，统一抽象为 content 与 pageable.totalRow
 */
export interface AfterQueryResult<D = any> {
  content?: D[]
  pageable?: { totalRow?: number } | any
  [key: string]: any
}

/**
 * 表格查询配置
 */
export interface TableQueryOptions<D = any> {
  /**
   *查询接口
   */
  queryApi: (params?: any) => Promise<any>
  /**
   * 是否加载中
   */
  loading: boolean
  /**
   * 数据源
   */
  dataSource: D[]
  /**
   * 查询参数
   */
  queryParams: Record<string, any>
  /**
   * 选择配置
   */
  rowSelections: TableRowSelectionsProps
  /**
   * 挂载时进行查询
   */
  queryOnMounted: boolean
  /**
   *  分页配置
   */
  pagination: TablePaginationProps
  /**
   * 是否展开
   */
  expand: boolean
  /**
   * 展开变化
   */
  expandChange: () => void
  /**
   * 查询前回调
   */
  beforeQuery: () => void | Promise<void>
  /**
   * 查询后回调：用于适配后端返回结构到组合式内部通用结构
   */
  afterQuery: (data: any) => AfterQueryResult<D> | Promise<AfterQueryResult<D>>
  /**
   * 原始列定义（用于列显隐与密度设置的通用处理）
   * 例如：[{ title: '名称', dataIndex: 'name' }, ...]
   */
  columns?: any[]
  /**
   * 列显隐中需要固定禁用的列 dataIndex（如 'action' 操作列）
   */
  columnDisabledKeys?: string[] | number[] | symbol[]
}

/**
 * 表格查询方法
 */
export function useTableQuery(_options: Partial<TableQueryOptions>) {
  // 防止 undefined 的 pagination 覆盖默认值
  const { pagination: optPagination, ...restOptions } = _options || {}

  // 默认分页配置，随后与传入分页进行浅合并
  const defaultPagination: TablePaginationProps = {
    pageNumber: 1,
    pageSize: 10,
    pageSizeOptions: ['10', '20', '50', '100'],
    total: 0,
    order: '',
    column: '',
    showSizeChanger: true,
    showQuickJumper: true,
    showTotal: total => `共有 ${total} 条数据`,
  }
  // 合并分页：避免 undefined 覆盖默认值
  const mergedPagination = assign({}, defaultPagination, optPagination || {})

  // 从 restOptions 中解构可覆盖的配置，确保 queryApi 等必填项有默认实现
  const {
    queryApi: optQueryApi,
    loading: optLoading,
    queryParams: optQueryParams,
    dataSource: optDataSource,
    rowSelections: optRowSelections,
    queryOnMounted: optQueryOnMounted,
    expand: optExpand,
    beforeQuery: optBeforeQuery,
    afterQuery: optAfterQuery,
    columns: optColumns,
    columnDisabledKeys: optColumnDisabledKeys,
  } = restOptions as Partial<TableQueryOptions>

  // 构建最终状态对象，避免 assign 将 undefined 覆盖默认值
  const state = reactive<TableQueryOptions>({
    // 查询 API：必须存在，默认返回已解析 Promise
    queryApi: optQueryApi ?? (() => Promise.resolve()),
    // 加载与数据状态
    loading: optLoading ?? false,
    dataSource: optDataSource ?? [],
    // 查询参数与挂载后是否自动查询
    queryParams: optQueryParams ?? {},
    queryOnMounted: optQueryOnMounted ?? true,
    // 分页配置
    pagination: mergedPagination,
    // 展开/收起状态与切换方法
    expand: optExpand ?? false,
    expandChange() {
      state.expand = !state.expand
    },
    // 查询前/后回调（默认空实现与直传）
    beforeQuery: optBeforeQuery ?? (() => {}),
    afterQuery: optAfterQuery ?? ((data: any) => data),
    // 列定义与禁用列 key
    columns: optColumns ?? [],
    columnDisabledKeys: optColumnDisabledKeys ?? ['action'],
    // 行选择（默认行为）：可被覆盖
    rowSelections: optRowSelections ?? {
      selectedRowKeys: [],
      selectedRows: [],
      onChange(selectedRowKeys: any[], selectedRows: any[]) {
        state.rowSelections.selectedRowKeys = selectedRowKeys
        state.rowSelections.selectedRows = selectedRows
      },
    },
  })
  // 设置分页变更回调（放在 state 创建之后使用闭包中的 state）
  state.pagination.onChange = (pageNumber, pageSize) => {
    state.pagination.pageSize = pageSize
    state.pagination.pageNumber = pageNumber
    query().then(() => {})
  }

  // 查询方法
  async function query() {
    if (state.loading)
      return
    state.loading = true

    try {
      await state.beforeQuery()
      const data = await state.queryApi({
        pageNumber: state.pagination.pageNumber,
        pageSize: state.pagination.pageSize,
        column: state.pagination.column,
        order: state.pagination.order,
        ...state.queryParams,
      })
      if (data) {
        const _data = await state.afterQuery(data)
        state.dataSource = _data.content ?? []
        state.pagination.total = _data.pageable?.totalRow ?? 0
      }
    }
    catch (e) {
      throw new Error(`Query Failed: ${e}`)
    }
    finally {
      state.loading = false
    }
  }

  // 重置方法
  function resetQuery() {
    state.pagination.pageNumber = 1
    state.queryParams = {}
    query().then(() => {})
  }

  // 初始化查询
  function initQuery() {
    state.pagination.pageNumber = 1
    query().then(() => {})
  }

  onMounted(() => {
    if (!state.queryOnMounted)
      return
    query().then(() => {})
  })

  // 刷新（保持当前分页与条件）
  function refresh() {
    query().then(() => {})
  }

  return {
    query,
    resetQuery,
    initQuery,
    refresh,
    state,
  }
}
