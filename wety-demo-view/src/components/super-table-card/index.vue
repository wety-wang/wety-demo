<script setup lang="ts">
// Super版表格卡片：封装查询表单 + ProTableCard
// 目标：页面仅提供列定义、查询接口与表单schema，即可快速落地列表页
import type { TableColumnType, TableProps } from 'ant-design-vue'
import type { TableRowSelection } from 'ant-design-vue/es/table/interface'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import type { AfterQueryResult } from '~@/composables/table-query'
import ProTableCard from '~@/components/pro-table-card/index.vue'
import QueryFormCard from '~@/components/query-form-card/index.vue'

interface Props<D = any> {
  // 标题（传递给内部 ProTableCard）
  title?: string
  // 列定义
  columns: TableColumnType<D>[]
  // 查询接口
  queryApi: (params?: any) => Promise<any>
  // 查询结果适配
  afterQuery?: (data: any) => AfterQueryResult<D> | Promise<AfterQueryResult<D>>
  // 查询表单schema
  formSchema: FormItemSchema[]
  // 初始查询参数（可选）
  queryParams?: Record<string, any>
  // ProTableCard 透传配置
  showRefresh?: boolean
  showColumnSetting?: boolean
  // 注意：这里与子组件类型保持一致（联合的数组类型），避免数组元素联合导致的类型不匹配
  columnDisabledKeys?: string[] | number[] | symbol[]
  pagination?: any
  queryOnMounted?: boolean
  // 表格额外属性透传（将被传给内部 ProTableCard 的 a-table）
  tableProps?: Omit<Partial<TableProps>, 'columns' | 'dataSource' | 'loading' | 'pagination' | 'size'> & { class?: any }
  // 表格装饰开关：条纹与斑马（奇偶行样式）
  striped?: boolean
  zebra?: boolean
  // 自定义类名（与 ProTableCard 保持一致）
  stripedClass?: string
  zebraRowClassName?: string
  // 是否开启行选择（开启后展示选择列并透传 selection-change 事件）
  enableRowSelection?: boolean
  // 行选择额外配置，透传到内部 ProTableCard（如 type: 'radio'、getCheckboxProps 等）
  rowSelectionProps?: Partial<TableRowSelection>
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  queryParams: () => ({}),
  showRefresh: true,
  showColumnSetting: true,
  queryOnMounted: true,
  tableProps: () => ({}),
  striped: false,
  zebra: false,
  stripedClass: 'ant-table-striped',
  zebraRowClassName: 'table-striped',
  enableRowSelection: false,
  rowSelectionProps: () => ({}),
})

// 组件事件：选择变化透传给父组件（与内部 ProTableCard 保持一致）
const emit = defineEmits<{
  /**
   * 表格选择变化事件
   * @param e 事件对象（与内部 ProTableCard 保持一致）
   * @param selectedRowKeys 选中行主键集合
   * @param selectedRows 选中行数据集合
   */
  (e: 'selection-change', selectedRowKeys: any[], selectedRows: any[]): void
}>()

// 查询表单组件引用：用于调用重置
const queryFormRef = ref<InstanceType<typeof QueryFormCard> | null>(null)

// 本地查询参数：根据 schema 初始化，合并外部初始值
const queryParams = reactive<Record<string, any>>({})
function initLocalQueryParams() {
  // 根据 schema 的字段名初始化为 undefined，避免数据泥团
  for (const item of props.formSchema) {
    if (!(item.name in queryParams))
      queryParams[item.name] = undefined
  }
  // 合并外部初始值
  Object.assign(queryParams, props.queryParams || {})
}
initLocalQueryParams()

// 当外部 queryParams 变化时，同步到本地
watch(
  () => props.queryParams,
  (val) => {
    Object.assign(queryParams, val || {})
  },
  { deep: true },
)

// 内部 ProTableCard 引用，用于触发查询与重置
const tableRef = ref<InstanceType<typeof ProTableCard> | null>(null)

// 触发查询：从第 1 页开始
function initQuery() {
  tableRef.value?.initQuery()
}

// 处理来自 QueryFormCard 的事件（避免在模板中直接访问 .value 导致类型问题）
// 接收 QueryFormCard 的最新查询条件并同步，保证表格查询入参正确
function handleFormSubmit(params?: Record<string, any>) {
  // 将提交的查询条件合并到本地 queryParams
  Object.assign(queryParams, params || {})
  tableRef.value?.initQuery()
}
function handleFormReset() {
  tableRef.value?.initQuery()
}

// 重置查询：清空表单并重新查询
function resetQuery() {
  // 通过子查询组件执行重置（包含清理校验状态）
  queryFormRef.value?.reset()
  tableRef.value?.initQuery()
}

// 选择变化事件处理：直接向父组件透传
function handleSelectionChange(selectedRowKeys: any[], selectedRows: any[]) {
  emit('selection-change', selectedRowKeys, selectedRows)
}

// 暴露方法给父组件使用
defineExpose({ initQuery, resetQuery, queryParams })

// 仅当父组件透传了 form-advanced-extra 插槽时，才继续向 QueryFormCard 透传
// 否则 QueryFormCard 的 useSlots()['form-advanced-extra'] 会始终为真，导致 UI 误判有高级区
const slots = useSlots()
const hasFormAdvancedExtra = computed(() => !!slots['form-advanced-extra'])
</script>

<template>
  <div>
    <!-- 查询条件卡片：独立组件，支持校验与插槽扩展 -->
    <QueryFormCard
      ref="queryFormRef"
      :form-schema="props.formSchema"
      :model-value="queryParams"
      @update:model-value="(v) => Object.assign(queryParams, v || {})"
      @submit="handleFormSubmit"
      @reset="handleFormReset"
    >
      <!-- 透传自定义查询字段插槽 -->
      <template #form-extra="{ queryParams: qp, setParam }">
        <slot name="form-extra" :query-params="qp" :set-param="setParam" />
      </template>
      <!-- 透传高级区域自定义查询字段插槽 -->
      <template v-if="hasFormAdvancedExtra" #form-advanced-extra="{ queryParams: qp, setParam }">
        <slot name="form-advanced-extra" :query-params="qp" :set-param="setParam" />
      </template>
      <!-- 透传查询操作扩展插槽 -->
      <template #query-actions="{ queryParams: qp }">
        <slot name="query-actions" :query-params="qp" />
      </template>
    </QueryFormCard>

    <!-- 通用表格卡片：透传列、查询接口与查询参数 -->
    <ProTableCard
      ref="tableRef"
      :title="props.title"
      :columns="props.columns"
      :query-params="queryParams"
      :query-api="props.queryApi"
      :after-query="props.afterQuery"
      :show-refresh="props.showRefresh"
      :show-column-setting="props.showColumnSetting"
      :column-disabled-keys="props.columnDisabledKeys"
      :pagination="props.pagination"
      :query-on-mounted="props.queryOnMounted"
      :table-props="props.tableProps"
      :striped="props.striped"
      :zebra="props.zebra"
      :striped-class="props.stripedClass"
      :zebra-row-class-name="props.zebraRowClassName"
      :enable-row-selection="props.enableRowSelection"
      :row-selection-props="props.rowSelectionProps"
      @selection-change="handleSelectionChange"
    >
      <!-- 透传工具栏扩展插槽 -->
      <template #extra>
        <slot name="table-extra" />
      </template>
      <!-- 透传表格单元格插槽：允许页面自定义列单元格内容，如操作列 -->
      <template #bodyCell="{ column, record, index }">
        <slot name="bodyCell" :column="column" :record="record" :index="index" />
      </template>
    </ProTableCard>
  </div>
</template>

<style scoped>
/* 保持样式轻量，遵循全局主题 */
</style>
