<script setup lang="ts">
// 可复用的表格卡片组件，内置刷新、列设置等工具栏
// 通过组合式 useTableQuery 统一管理查询、分页、列显隐
import type { TableColumnType, TableProps } from 'ant-design-vue'
import type { TableRowSelection } from 'ant-design-vue/es/table/interface'
import type { AfterQueryResult } from '~@/composables/table-query'
import { ReloadOutlined, SettingOutlined } from '@ant-design/icons-vue'
import { useTableQuery } from '~@/composables/table-query'

interface Props<D = any> {
  // 卡片标题
  title?: string
  // 原始列定义（用于列显隐与过滤）
  columns: TableColumnType<D>[]
  // 查询接口：入参包含分页、排序与外部查询参数
  queryApi: (params?: any) => Promise<any>
  // 查询后适配统一结构：content 与 pageable.totalRow
  afterQuery?: (data: any) => AfterQueryResult<D> | Promise<AfterQueryResult<D>>
  // 列设置中需要禁用勾选的列 dataIndex（可多个，如操作列等）
  columnDisabledKeys?: string[] | number[] | symbol[]
  // 外部查询参数（通常由查询表单维护）
  queryParams?: Record<string, any>
  // 分页自定义（覆盖默认值）
  pagination?: any
  // 是否在挂载时自动查询
  queryOnMounted?: boolean
  // 工具栏可选项：默认全部显示
  showRefresh?: boolean
  showColumnSetting?: boolean
  // 透传给 a-table 的额外参数（如 scroll、bordered、rowClassName、class 等）
  // 为避免覆盖内部绑定，建议不要在此传入 columns/dataSource/loading/pagination/size
  tableProps?: Omit<Partial<TableProps>, 'columns' | 'dataSource' | 'loading' | 'pagination' | 'size'> & { class?: any }
  // 表格装饰：条纹背景（为表格添加额外 class）与斑马纹（奇偶行不同样式）
  striped?: boolean
  zebra?: boolean
  // 可自定义条纹/斑马的类名（默认 'ant-table-striped' 与 'table-striped'）
  stripedClass?: string
  zebraRowClassName?: string
  // 是否开启行选择（开启后会展示选择列并透传 selection-change 事件）
  enableRowSelection?: boolean
  // 行选择额外配置（如 type: 'radio'、getCheckboxProps 等），不建议传 selectedRowKeys/selectedRows
  rowSelectionProps?: Partial<TableRowSelection>
}

const props = withDefaults(defineProps<Props<any>>(), {
  title: '',
  queryParams: () => ({}),
  queryOnMounted: true,
  showRefresh: true,
  showColumnSetting: true,
  // 默认禁用“操作”列（dataIndex 或 key 为 'action'）
  columnDisabledKeys: () => ['action'],
  tableProps: () => ({}),
  striped: true,
  zebra: false,
  stripedClass: 'ant-table-striped',
  zebraRowClassName: 'table-striped',
  enableRowSelection: false,
  rowSelectionProps: () => ({}),
})

// 组件事件：选择变化透传给父组件
// 使用 kebab-case 事件名，模板中为 @selection-change
const emit = defineEmits<{
  /**
   * 表格选择变化事件
   * @param selectedRowKeys 选中行主键集合
   * @param selectedRows 选中行数据集合
   */
  (e: 'selection-change', selectedRowKeys: any[], selectedRows: any[]): void
}>()

// 使用通用表格查询与交互组合式
const {
  state,
  initQuery,
  resetQuery,
  refresh,
} = useTableQuery({
  columns: props.columns,
  queryApi: props.queryApi,
  afterQuery: props.afterQuery,
  columnDisabledKeys: props.columnDisabledKeys,
  pagination: props.pagination,
  queryOnMounted: props.queryOnMounted,
})

// 合并行选择配置：在原有 onChange 基础上追加事件透传
// 注意：避免深拷贝，保留原对象引用，仅覆盖 onChange 回调
const rowSelectionWithEmit = computed(() => {
  const base = state.rowSelections
  const extra = props.rowSelectionProps || {}
  return {
    // 透传原始选择配置与额外配置（如单选/禁用某些行）
    ...base,
    ...extra,
    // 当选择变化时，先同步 state，再透传事件给父组件
    onChange(selectedRowKeys: any[], selectedRows: any[]) {
      // 调用原始 onChange 保持内部状态一致
      base?.onChange?.(selectedRowKeys, selectedRows)
      // 透传给父组件
      emit('selection-change', selectedRowKeys, selectedRows)
    },
  }
})

// 合并 tableProps 与条纹/斑马配置，避免外部覆盖内部绑定
const tablePropsMerged = computed(() => {
  const base = props.tableProps || {}
  const classes: string[] = []
  // 兼容外部传入 class（可能是字符串或数组），统一处理为字符串
  if (typeof (base as any).class === 'string')
    classes.push((base as any).class)
  else if (Array.isArray((base as any).class))
    classes.push(...(base as any).class)
  // 条纹：追加默认条纹类名（可自定义）
  if (props.striped)
    classes.push(props.stripedClass || 'ant-table-striped')
  const mergedClass = classes.filter(Boolean).join(' ').trim() || undefined

  // 斑马：与外部 rowClassName 函数合并，保留外部行为
  const userRowClassName = (base as any).rowClassName
  const zebraClass = props.zebraRowClassName || 'table-striped'
  const enableZebra = !!props.zebra
  const composedRowClassName = (record: any, index: number) => {
    // 外部 rowClassName 支持：字符串或函数
    const userClass = typeof userRowClassName === 'function'
      ? userRowClassName(record, index)
      : (typeof userRowClassName === 'string' ? userRowClassName : '')
    const zebra = enableZebra && (index % 2 === 1) ? zebraClass : ''
    return [userClass, zebra].filter(Boolean).join(' ').trim()
  }

  return {
    ...base,
    class: mergedClass,
    rowClassName: (userRowClassName || enableZebra) ? composedRowClassName : userRowClassName,
  }
})

// =============================
// 工具栏交互逻辑
// =============================
// 列设置：控制列显隐
// 原始列集合（由 props.columns 提供），使用 shallowRef 避免不必要的深层响应
const dropdownVisible = ref(false)
const columns = shallowRef<TableColumnType<any>[]>(props.columns ?? [])
// 勾选选项生成：支持禁用多个列
const disabledSet = computed(() => {
  return new Set(props.columnDisabledKeys?.filter(v => v !== undefined && v !== null) || [])
})
// 列键解析：优先 dataIndex，其次 key，最后使用稳定的索引兜底
function resolveColumnKey(item: TableColumnType<any>, index: number): string | number {
  const key = (item as any)?.dataIndex ?? (item as any)?.key
  // 兜底：防止 undefined 导致 AntDV 内部对 value.toString() 报错
  return key ?? `__col_${index}`
}
const columnOptions = computed(() => columns.value.map((item, idx) => ({
  label: item.title as any,
  value: resolveColumnKey(item, idx),
  // 禁用判断同时支持 dataIndex 与 key 两种键
  disabled: disabledSet.value.has((item as any)?.dataIndex) || disabledSet.value.has((item as any)?.key),
})))
// 当前所有列的 dataIndex 列表（作为默认全选）
const getCheckList = computed(() => columns.value.map((item, idx) => resolveColumnKey(item, idx)))
// 禁用列在当前列集合中的有效列表（仅包含存在于 columns 的禁用项）
const disabledList = computed<(string | number)[]>(() => {
  const list: Array<string | number> = []
  columns.value.forEach((item, idx) => {
    const di = (item as any)?.dataIndex
    const k = (item as any)?.key
    if (disabledSet.value.has(di) || disabledSet.value.has(k)) {
      list.push(resolveColumnKey(item, idx))
    }
  })
  return list
})
// 选择状态：半选、全选、具体选中项
const columnSelectState = reactive({
  indeterminate: false,
  checkAll: true,
  checkList: getCheckList.value,
})
// 根据选择项过滤需要显示的列
function filterAction(values: (string | number)[]) {
  // 提前返回减少嵌套
  if (!values?.length)
    return []
  return columns.value.filter((item, idx) => values.includes(resolveColumnKey(item, idx)))
}
// 初始化可见列集合（默认全选）
const filterColumns = ref<TableColumnType<any>[]>(filterAction(getCheckList.value))

// 全选/反选事件：维护选择状态与列显隐
function handleCheckAllChange(e: any) {
  const checked = !!e?.target?.checked
  // 关键：全选/反选时始终保留禁用列选中状态
  const baseList = checked ? getCheckList.value : []
  const nextList = Array.from(new Set([...(baseList as any[]), ...disabledList.value]))
  Object.assign(columnSelectState, {
    checkList: nextList,
    indeterminate: !checked && !!nextList.length,
  })
  filterColumns.value = filterAction(nextList)
}
// 监听选择项变化：保持半选与全选状态，并实时同步可见列
watch(
  () => columnSelectState.checkList,
  (val) => {
    const total = getCheckList.value.length
    const len = val.length
    columnSelectState.indeterminate = !!len && len < total
    columnSelectState.checkAll = len === total
    filterColumns.value = filterAction(val as any[])
  },
)
// 重置列选择：恢复默认全选
function handleResetChange() {
  columnSelectState.checkList = getCheckList.value
  filterColumns.value = filterAction(getCheckList.value)
}
// 勾选事件：直接同步到可见列集合
function handleCheckChange(value: any) {
  // 关键：勾选变化时也强制包含禁用列，避免被意外移除
  const enforced = Array.from(new Set([...(value as any[]), ...disabledList.value]))
  columnSelectState.checkList = enforced
  filterColumns.value = filterAction(enforced)
}

// 监听外部列定义变化：保持列选项与可见列同步
watch(
  () => props.columns,
  (newCols) => {
    columns.value = newCols ?? []
    // 重新构建选择列表与可见列，避免数据泥团不同步
    const nextList = columns.value.map((item, idx) => resolveColumnKey(item, idx))
    columnSelectState.checkList = nextList
    columnSelectState.indeterminate = false
    columnSelectState.checkAll = true
    filterColumns.value = filterAction(nextList)
  },
  { deep: true },
)

// 同步外部查询参数到组合式内部
watch(
  () => props.queryParams,
  (val) => {
    // 将外部查询条件透传给组合式，参与查询入参
    state.queryParams = val || {}
  },
  { deep: true, immediate: true },
)

// 暴露查询方法，便于父组件控制查询与重置
defineExpose({ initQuery, resetQuery, refresh, state })

// =============================
// Table 排序变更：更新分页的 column 与 order 并重新查询
// =============================
function handleTableChange(_pagination: any, _filters: any, sorter: any, extra: any) {
  // 仅在排序动作时处理，避免与分页 onChange 重复触发查询
  if (extra?.action !== 'sort')
    return
  // 兼容多列排序与单列排序
  const s = Array.isArray(sorter) ? sorter[0] : sorter
  // AntDV：order 为 'ascend' | 'descend' | undefined
  const orderMap: Record<string, string> = { ascend: 'asc', descend: 'desc' }
  const nextOrder = orderMap[s?.order as string] ?? ''
  const nextColumn = (s?.field ?? s?.columnKey) ?? ''
  // 当清空排序时，置空后端排序参数（让后端不排序或走默认）
  state.pagination.order = nextOrder
  state.pagination.column = nextOrder ? nextColumn : ''
  // 排序通常从第一页开始
  initQuery()
}
// 表格列宽度调整：同步更新 columns 定义
function handleResizeColumn(w: number, col: TableColumnType<any>) {
  col.width = w
}
</script>

<template>
  <!-- 表格卡片：包含刷新、密度、列设置；支持插入额外操作 -->
  <a-card :title="props.title">
    <template #extra>
      <a-space size="middle">
        <!-- 插槽：允许追加自定义操作按钮到工具栏 -->
        <slot name="extra" />
        <!-- 刷新按钮：触发重新查询，保留当前条件与分页 -->
        <a-tooltip v-if="props.showRefresh" title="刷新">
          <ReloadOutlined @click="refresh" />
        </a-tooltip>
        <!-- 列设置：支持列显隐与重置，将选中项映射到最终 columns -->
        <a-tooltip v-if="props.showColumnSetting" title="列设置">
          <a-dropdown v-model:open="dropdownVisible" trigger="click">
            <SettingOutlined />
            <template #overlay>
              <a-card>
                <template #title>
                  <!-- 全选与半选状态控制 -->
                  <a-checkbox v-model:checked="columnSelectState.checkAll" :indeterminate="columnSelectState.indeterminate" @change="handleCheckAllChange">
                    列选择
                  </a-checkbox>
                </template>
                <template #extra>
                  <!-- 重置列选择（恢复默认全选） -->
                  <a-button type="link" @click="handleResetChange">
                    重置
                  </a-button>
                </template>
                <!-- 勾选列显隐：同步 checkList 到 filterColumns -->
                <a-checkbox-group v-model:value="columnSelectState.checkList" :options="columnOptions" style="display: flex; flex-direction: column;" @change="handleCheckChange" />
              </a-card>
            </template>
          </a-dropdown>
        </a-tooltip>
      </a-space>
    </template>
    <!-- 表格渲染：绑定统一的 loading、columns、dataSource 与 pagination；支持额外 tableProps 透传 -->
    <!-- 注意：将 v-bind="props.tableProps" 放在前面，后续绑定覆盖重复项，避免外部覆盖核心绑定 -->
    <a-table
      v-bind="tablePropsMerged"
      :loading="state.loading"
      :columns="filterColumns"
      :data-source="state.dataSource"
      :pagination="state.pagination"
      :row-selection="props.enableRowSelection ? rowSelectionWithEmit : undefined"
      @change="handleTableChange"
      @resize-column="handleResizeColumn"
    >
      <!-- 透传表格单元格插槽：允许父组件自定义单元格内容（如操作列） -->
      <template #bodyCell="{ column, record, index }">
        <slot name="bodyCell" :column="column" :record="record" :index="index" />
      </template>
    </a-table>
  </a-card>
</template>

<style scoped>
</style>
