# ProTableCard 使用说明

ProTableCard 是一个可复用的“通用表格卡片”组件，统一封装了刷新、列设置、远程排序与查询分页交互。它通过组合式 `useTableQuery` 管理查询状态，并支持将原生 `a-table` 的部分属性通过 `tableProps` 透传。

## 组件定位
- 封装通用的表格工具栏与交互逻辑
- 管理查询、分页、排序、列显隐
- 透传 `a-table` 额外属性，保证灵活性

## 快速开始（最小用法）
```vue
<script setup lang="ts">
import ProTableCard from '~@/components/pro-table-card/index.vue'
import type { TableColumnType } from 'ant-design-vue'
import { listLoginLogPageable } from '~@/api/audit-log/login'

const columns: TableColumnType<any>[] = [
  { title: '登录名', dataIndex: 'accountLoginName', sorter: true }, // 开启远程排序
  { title: '时间', dataIndex: 'operationTime', width: 200 },
]

const queryParams = reactive({})
</script>

<template>
  <ProTableCard
    title="登录日志"
    :columns="columns"
    :query-api="listLoginLogPageable"
    :query-params="queryParams"
    :after-query="(data) => ({ content: data?.content, pageable: data?.pageable })"
  />
</template>
```

## Props
- `title?: string` 卡片标题。
- `columns: TableColumnType[]` 原始列定义（用于列显隐与过滤）。
- `queryApi: (params?: any) => Promise<any>` 查询接口，统一入参结构：
  - `pageNumber`, `pageSize`（分页）
  - `column`, `order`（排序，`asc`/`desc`）
  - `...queryParams`（外部查询字段）
- `afterQuery?: (data: any) => { content?: any[]; pageable?: { totalRow?: number } }` 查询后适配，建议返回 `{ content, pageable.totalRow }`。
- `columnDisabledKeys?: (string|number|symbol)[]` 在“列设置”中禁用的列 dataIndex（例如操作列）。
- `queryParams?: Record<string, any>` 外部查询参数对象（通常来自查询表单）。
- `pagination?: any` 分页覆盖（可设置默认排序：`{ column: 'operationTime', order: 'desc' }`）。
- `queryOnMounted?: boolean` 挂载后自动查询，默认 `true`。
- `showRefresh?: boolean` 是否显示刷新按钮，默认 `true`。
- `showColumnSetting?: boolean` 是否显示列设置，默认 `true`。
- `tableProps?: Partial<TableProps>` 透传到 `a-table` 的额外属性：
  - 推荐：`scroll`, `bordered`, `rowClassName`, `class`
  - 避免传入：`columns`/`dataSource`/`loading`/`pagination`/`size`（由内部绑定）
- `striped?: boolean` 是否开启表格条纹背景（追加类名），默认 `false`。
- `zebra?: boolean` 是否开启斑马纹（奇数行追加类名），默认 `false`。
- `stripedClass?: string` 条纹背景类名，默认 `ant-table-striped`。
- `zebraRowClassName?: string` 斑马纹行类名，默认 `table-striped`。
- `enableRowSelection?: boolean` 是否开启行选择与 `selection-change` 事件，默认 `false`。
- `rowSelectionProps?: Partial<TableRowSelection>` 行选择额外配置（如 `type: 'radio'`、`getCheckboxProps`）。不建议传 `selectedRowKeys/selectedRows` 以避免受控冲突。

## 远程排序
- 在列定义中设置 `sorter: true` 即可显示排序图标并开启远程排序。
- 组件在 `@change` 中提取排序字段与方向：将 AntDV 的 `'ascend'/'descend'` 映射为后端的 `'asc'/'desc'`。
- 排序会写入到 `state.pagination.column/order` 并从第一页重新查询。

示例：
```ts
const columns = [
  { title: '登录名', dataIndex: 'accountLoginName', sorter: true },
  { title: '时间', dataIndex: 'operationTime', sorter: true },
]
```

## 工具栏功能
- 刷新：点击刷新按钮调用 `refresh()`，保留当前分页与条件。
- 列设置：勾选列显隐，禁用列始终保持选中；支持“重置”恢复全选。

## 插槽
- `#extra` 工具栏右侧扩展区域，追加按钮或说明。
- `#bodyCell` 表格单元格内容自定义（作用域参数：`{ column, record, index }`）。

实现说明（内部实现片段）：
```vue
<!-- 在 ProTableCard 内部，bodyCell 插槽嵌入到 a-table，使父组件可自定义单元格渲染 -->
<a-table ...>
  <template #bodyCell="{ column, record, index }">
    <slot name="bodyCell" :column="column" :record="record" :index="index" />
  </template>
</a-table>
```

## 暴露方法
- `initQuery()` 从第一页查询（常用于表单提交后触发）。
- `resetQuery()` 清空查询参数并从第一页查询。
- `refresh()` 保持当前分页与条件重新查询。
- `state` 暴露内部状态（包含 `loading`、`dataSource`、`pagination`、`queryParams`）。

示例：父组件通过 `ref` 控制查询
```vue
<ProTableCard ref="tableRef" ... />
<!-- 在父组件中： -->
tableRef.value?.initQuery()
```

## 表格属性透传（tableProps）
- 你可以定制原生 `a-table` 的行为和样式：
```vue
<ProTableCard
  :columns="columns"
  :query-api="listLoginLogPageable"
  :query-params="queryParams"
  :table-props="{ scroll: { x: 1500 }, bordered: true }"
  :striped="true"
  :zebra="true"
/>
```

条纹样式示例：
```css
.ant-table-striped .table-striped td { background-color: #fafafa; }
```

## 注意事项
- 组件会深度监听 `queryParams` 并传给后端；确保字段名与后端参数一致。
- 默认分页：`pageNumber=1`，`pageSize=10`，默认排序：`column='createTime'`，`order='desc'`（可通过 `pagination` 覆盖）。
- 多列排序场景下，当前实现以第一个排序条件为主（如需多列，请扩展后端与组件策略）。

## 复杂插槽示例与说明

### 1) 工具栏扩展：批量操作（导出/删除选中）
```vue
<script setup lang="ts">
// 通过 ref 访问 ProTableCard 暴露的 state（含选择状态）
const tableRef = ref<InstanceType<typeof ProTableCard> | null>(null)

function exportSelected() {
  // 选中行列表（中文注释解释访问方式）
  const rows = tableRef.value?.state.rowSelections.selectedRows || []
  if (!rows.length)
    return message.warning('请先选择要导出的行')
  // 调用后端导出接口（示例：传选中行的主键 ID）
  const ids = rows.map(r => r.id)
  // exportByIds(ids)
}

function removeSelected() {
  const keys = tableRef.value?.state.rowSelections.selectedRowKeys || []
  if (!keys.length)
    return message.warning('请先选择要删除的行')
  // 调用后端批量删除接口
  // deleteByIds(keys)
}
</script>

<ProTableCard ref="tableRef" :columns="columns" :query-api="listLoginLogPageable" :query-params="queryParams">
  <template #extra>
    <!-- 追加到工具栏右侧区域：批量导出与删除 -->
    <a-space>
      <a-button type="default" @click="exportSelected">导出选中</a-button>
      <a-button type="primary" danger @click="removeSelected">删除选中</a-button>
    </a-space>
  </template>
</ProTableCard>
```

要点：
- `#extra` 插槽位于工具栏右侧，适用于批量/额外操作。
- 通过 `ref` 访问 `tableRef.value.state.rowSelections` 获取选中行与主键集合。

### 2) 工具栏扩展：条件导出（基于当前查询对象）
```vue
<script setup lang="ts">
function exportByParams() {
  const params = { ...(tableRef.value?.state.queryParams || {}) }
  // 调用后端导出接口（基于当前查询条件）
  // exportLoginLog(params)
}
</script>

<ProTableCard ref="tableRef" :columns="columns" :query-api="listLoginLogPageable" :query-params="queryParams">
  <template #extra>
    <a-button type="default" @click="exportByParams">导出当前条件</a-button>
  </template>
</ProTableCard>
```

要点：
- 通过 `ref` 读取 `state.queryParams`，实现与列表条件一致的导出逻辑。

### 3) bodyCell：自定义“操作列”与复杂渲染
```vue
<script setup lang="ts">
import ProTableCard from '~@/components/pro-table-card/index.vue'
import { listUserPageable } from '~@/api/org/user'
import type { TableColumnType } from 'ant-design-vue'

// 列定义：为操作列设置唯一 key，便于插槽内识别
const columns: TableColumnType<any>[] = [
  { title: '用户名', dataIndex: 'username' },
  { title: '状态', dataIndex: 'status' },
  { title: '操作', key: 'action', width: 160 },
]

function onEdit(row: any) { /* 编辑当前行 */ }
function onDelete(row: any) { /* 删除后刷新 */ }
</script>

<template>
  <ProTableCard :columns="columns" :query-api="listUserPageable">
    <template #bodyCell="{ column, record }">
      <!-- 操作列：提供编辑/删除按钮 -->
      <template v-if="column.key === 'action'">
        <a-space>
          <a-button type="link" @click="onEdit(record)">编辑</a-button>
          <a-popconfirm title="确认删除？" @confirm="onDelete(record)">
            <a-button type="link" danger>删除</a-button>
          </a-popconfirm>
        </a-space>
      </template>
      <!-- 状态列：示例将原始值映射为彩色标签 -->
      <a-tag v-else-if="column.dataIndex === 'status'" :color="record.status === 'enabled' ? 'green' : 'red'">
        {{ record.status === 'enabled' ? '启用' : '停用' }}
      </a-tag>
    </template>
  </ProTableCard>
</template>
```

要点：
- 建议为“操作列”设置 `key: 'action'`，在插槽内通过 `column.key` 精准匹配。
- 其它列可通过 `column.dataIndex` 条件渲染；未匹配时不输出内容即可使用默认渲染。
- 避免在插槽中直接修改 `record`，保持只读以防止影响内部渲染与性能。

## 选择事件与行选择

- 事件：`selection-change`（当选择变化时触发）
  - 参数：`(selectedRowKeys: any[], selectedRows: any[])`
  - 行选择依赖 `rowKey`，请通过 `tableProps` 传入唯一主键字段（如 `id`）。

### 开启/关闭选择与事件
```vue
<ProTableCard
  :columns="columns"
  :query-api="listLoginLogPageable"
  :table-props="{ rowKey: 'id' }"
  :enable-row-selection="true"  <!-- 开启选择与事件透传 -->
  @selection-change="onSelectionChange"
/>
```

将 `enableRowSelection` 置为 `false`（默认）时，不展示选择列，也不会触发 `selection-change`。

### 最小示例：监听选择变化
```vue
<script setup lang="ts">
import ProTableCard from '~@/components/pro-table-card/index.vue'
import { listLoginLogPageable } from '~@/api/audit-log/login'

const columns = [
  { title: '登录名', dataIndex: 'accountLoginName' },
  { title: '时间', dataIndex: 'operationTime' },
]

function onSelectionChange(selectedRowKeys: any[], selectedRows: any[]) {
  // 中文注释：监听行选择变化，拿到选中主键与行数据
  console.log('keys:', selectedRowKeys, 'rows:', selectedRows)
}
</script>

<template>
  <ProTableCard
    :columns="columns"
    :query-api="listLoginLogPageable"
    :table-props="{ rowKey: 'id' }"  <!-- 需设置主键字段，确保选择正确 -->
    :enable-row-selection="true"
    @selection-change="onSelectionChange"
  />

</template>
```

### 在父组件中持久化选择状态（用于批量操作）
```vue
<script setup lang="ts">
const selectedKeys = ref<any[]>([])
const selectedRows = ref<any[]>([])

function onSelectionChange(keys: any[], rows: any[]) {
  // 中文注释：将选择状态保存到父组件，便于批量导出/删除
  selectedKeys.value = keys
  selectedRows.value = rows
}

function exportSelected() {
  if (!selectedKeys.value.length)
    return message.warning('请先选择要导出的行')
  // 调用后端导出接口
  // exportByIds(selectedKeys.value)
}
</script>

<template>
  <ProTableCard :columns="columns" :query-api="listLoginLogPageable" :table-props="{ rowKey: 'id' }" @selection-change="onSelectionChange">
    <template #extra>
      <a-button @click="exportSelected">导出选中</a-button>
    </template>
  </ProTableCard>
</template>
```

### 高级：自定义选择行为（单选/禁用某些行）
> 推荐使用 `rowSelectionProps` 进行配置；也可通过 `ref.state.rowSelections` 进行更细粒度控制。

```vue
<script setup lang="ts">
const tableRef = ref<InstanceType<typeof ProTableCard> | null>(null)

onMounted(() => {
  // 中文注释：将勾选改为单选（radio），不影响事件透传
  if (tableRef.value) {
    tableRef.value.state.rowSelections.type = 'radio' // 单选
    // 禁用条件：示例将 result 为 'success' 的行禁用
    tableRef.value.state.rowSelections.getCheckboxProps = (record: any) => ({
      disabled: record.result === 'success',
    })
  }
})
</script>

<template>
  <ProTableCard
    ref="tableRef"
    :columns="columns"
    :query-api="listLoginLogPageable"
    :table-props="{ rowKey: 'id' }"
    :enable-row-selection="true"
    :row-selection-props="{ type: 'radio' }"
  />
</template>
```

注意事项：
- 仅当 `enableRowSelection` 为 `true` 时，才会展示选择列并触发 `selection-change`。
- 请勿覆盖 `rowSelections.onChange`；组件已包装该回调用于同步内部状态并向父组件发出 `selection-change`。
- 不建议在 `tableProps` 直接传 `rowSelection`，请改用 `rowSelectionProps` 进行配置，避免状态不一致。
