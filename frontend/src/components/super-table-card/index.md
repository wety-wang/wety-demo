# SuperTableCard 使用说明

SuperTableCard 是一种“页面快速搭建”的组合组件，内部封装了查询表单（QueryFormCard）与通用表格卡片（ProTableCard）。你只需要提供列定义、查询接口和表单 schema，即可快速落地标准列表页。

## 组件定位
- 组合查询表单与表格渲染，统一查询、分页、排序与参数传递
- 支持基础/高级查询项、校验、插槽扩展与工具栏扩展
- 通过 `tableProps` 透传原始 `a-table` 额外属性（如 `scroll`、`bordered`、`rowClassName`、`class`）

## 快速开始（最小用法）
```vue
<script setup lang="ts">
import SuperTableCard from '~@/components/super-table-card/index.vue'
import type { TableColumnType } from 'ant-design-vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

// 1) 列定义（可设置 sorter 开启远程排序）
const columns: TableColumnType<any>[] = [
  { title: '登录名', dataIndex: 'accountLoginName', sorter: true },
  { title: '时间', dataIndex: 'operationTime', width: 200 },
]

// 2) 查询表单 schema（基础/高级字段）
const formSchema: FormItemSchema[] = [
  { name: 'accountLoginName', label: '登录名', type: 'input', required: true },
  { name: 'result', label: '结果', type: 'select', advanced: true, options: [
    { label: '成功', value: 'success' }, { label: '失败', value: 'faild' }
  ] },
]

// 3) 查询接口（返回结构建议适配为 content + pageable.totalRow）
import { listLoginLogPageable } from '~@/api/audit-log/login'
</script>

<template>
  <SuperTableCard
    title="登录日志"
    :columns="columns"
    :form-schema="formSchema"
    :query-api="listLoginLogPageable"
  />
</template>
```

## Props
- `title?: string` 页面/卡片标题，透传给内部 ProTableCard。
- `columns: TableColumnType[]` 列定义，支持 `sorter: true` 开启远程排序。
- `queryApi: (params?: any) => Promise<any>` 查询接口。入参统一为：
  - `pageNumber`, `pageSize`（分页）
  - `column`, `order`（排序，`asc`/`desc`）
  - 以及当前查询条件（来自 QueryForm 的字段）
- `afterQuery?: (data: any) => { content?: any[]; pageable?: { totalRow?: number } }` 后处理，用于适配多样后端返回。
- `formSchema: FormItemSchema[]` 查询表单字段定义，见 QueryFormCard 文档。
- `queryParams?: Record<string, any>` 初始查询参数，将与 schema 初始化的本地对象合并。
- `showRefresh?: boolean` 是否显示刷新按钮，默认 `true`。
- `showColumnSetting?: boolean` 是否显示列设置，默认 `true`。
- `columnDisabledKeys?: (string|number|symbol)[]` 在列设置中禁用的列 dataIndex。
- `pagination?: any` 覆盖默认分页（可设置默认排序：`{ column: 'operationTime', order: 'desc' }`）。
- `queryOnMounted?: boolean` 挂载时自动查询，默认 `true`。
- `tableProps?: Partial<TableProps>` 透传给 `a-table` 的额外属性，推荐传入：
  - `scroll`, `bordered`, `rowClassName`, `class`
  - 避免传入 `columns`/`dataSource`/`loading`/`pagination`/`size`（由内部管理）
- `striped?: boolean` 是否开启表格条纹背景（追加类名），默认 `false`。
- `zebra?: boolean` 是否开启斑马纹（奇数行追加类名），默认 `false`。
- `stripedClass?: string` 条纹背景类名，默认 `ant-table-striped`。
- `zebraRowClassName?: string` 斑马纹行类名，默认 `table-striped`。
- `enableRowSelection?: boolean` 是否开启行选择与 `selection-change` 事件，默认 `false`。
- `rowSelectionProps?: Partial<TableRowSelection>` 行选择额外配置（如 `type: 'radio'`、`getCheckboxProps`）。不建议传 `selectedRowKeys/selectedRows`。

## 查询参数流与交互
- 本地 `queryParams` 会按 `formSchema` 初始化各字段为 `undefined`，并与外部 `queryParams` 合并。
- QueryFormCard 使用 `v-model` 双向绑定；提交时会通过 `submit(params)` 事件携带“完整查询条件”。
- 组件内部在 `handleFormSubmit(params)` 中将 `params` 合并到本地 `queryParams`，随后从第一页触发查询（`initQuery()`）。
- ProTableCard 深度监听 `queryParams` 引用，`useTableQuery` 在调用后端时会展开所有查询键值。

## 插槽
- `#form-extra` 传递到 QueryFormCard 的基础区域，用于拓展自定义字段。
- `#form-advanced-extra` 传递到 QueryFormCard 的高级区域，自定义高级字段。
- `#query-actions` 传递到 QueryFormCard 的操作区，追加按钮或说明。
- `#table-extra` 传递到 ProTableCard 的工具栏右侧，追加自定义操作。
- `#bodyCell` 传递到 ProTableCard 的 `a-table` 的 `#bodyCell` 插槽，用于自定义单元格内容（如操作列、状态标签等）。

示例：在高级区域追加“部门”字段（含中文注释）
```vue
<SuperTableCard :columns="columns" :form-schema="formSchema" :query-api="listLoginLogPageable">
  <template #form-advanced-extra="{ queryParams, setParam }">
    <!-- 自定义高级字段：部门选择，使用 setParam 写入到查询对象 -->
    <a-form-item name="departmentId" label="部门">
      <a-select :options="deptOptions" :value="queryParams.departmentId" @change="v => setParam('departmentId', v)" />
    </a-form-item>
  </template>
</SuperTableCard>
```

## 表格属性透传（tableProps）
- 直接设置 `a-table` 原生属性：
```vue
<SuperTableCard
  :columns="columns"
  :form-schema="formSchema"
  :query-api="listLoginLogPageable"
  :table-props="{ scroll: { x: 1500 }, bordered: true }"
  :striped="true"
  :zebra="true"
/>
```
- 样式示例（条纹行）：
```css
.ant-table-striped .table-striped td { background-color: #fafafa; }
```

## 暴露方法
- `initQuery()` 从第一页查询。
- `resetQuery()` 调用内部 QueryFormCard 的重置，并从第一页查询。
- `queryParams` 暴露当前查询对象引用（可用于外部读取或调试）。

## 注意事项
- 保持表单字段 `name` 与后端接口的参数名一致。
- 日期/范围建议通过 `valueFormat` 输出字符串（避免传 `dayjs`）。
- 默认排序可通过 `pagination: { column, order }` 设置，表头点击排序会自动更新并查询。
- 若使用 `striped/zebra`，请在全局样式中定义对应类名（如 `.ant-table-striped .table-striped td { background: #fafafa; }`）。

## 复杂插槽示例与说明

### 1) form-advanced-extra：联动字段 + 远端选项加载
```vue
<script setup lang="ts">
// 模拟远端选项加载（部门 -> 团队）
const deptOptions = ref([{ label: '技术部', value: 1 }, { label: '产品部', value: 2 }])
const teamOptions = ref<{ label: string; value: number }[]>([])

// 根据部门联动加载团队选项（中文注释解释数据流）
async function fetchTeams(deptId?: number) {
  // 这里调用你的后端接口：/api/team/list?deptId=xxx
  // 为示例起见，使用本地模拟数据
  teamOptions.value = deptId === 1
    ? [{ label: '前端组', value: 11 }, { label: '后端组', value: 12 }]
    : [{ label: '产品一组', value: 21 }, { label: '产品二组', value: 22 }]
}
</script>

<SuperTableCard :columns="columns" :form-schema="formSchema" :query-api="listLoginLogPageable">
  <template #form-advanced-extra="{ queryParams, setParam }">
    <!-- 高级字段：部门选择，改变时联动团队选项 -->
    <a-form-item name="departmentId" label="部门">
      <a-select
        :options="deptOptions"
        :value="queryParams.departmentId"
        @change="async (v) => { setParam('departmentId', v); await fetchTeams(v); setParam('teamId', undefined) }"
      />
    </a-form-item>
    <!-- 高级字段：团队选择，选项受部门选择影响 -->
    <a-form-item name="teamId" label="团队">
      <a-select :options="teamOptions" :value="queryParams.teamId" @change="v => setParam('teamId', v)" />
    </a-form-item>
  </template>
</SuperTableCard>
```

要点：
- 插槽提供 `queryParams` 与 `setParam`，用以读取/写入查询对象。
- 通过部门变更触发远端加载团队选项，并重置 `teamId` 防止“脏数据”。

### 2) query-actions：追加“导出”与“快速填充”操作
```vue
<SuperTableCard :columns="columns" :form-schema="formSchema" :query-api="listLoginLogPageable">
  <template #query-actions="{ queryParams }">
    <!-- 导出当前查询条件（只读 queryParams，避免直接修改） -->
    <a-button type="default" @click="() => exportByParams({ ...queryParams })">导出</a-button>
    <!-- 快速填充示例：一键将结果置为'成功'，帮助用户快速过滤 -->
    <a-button type="link" @click="() => (queryParams.result = 'success')">快速筛选成功</a-button>
  </template>
</SuperTableCard>
```

要点：
- `query-actions` 插槽仅用于追加按钮/说明；导出时建议复制参数对象传入，避免意外修改。

### 3) table-extra：工具栏右侧追加操作（父级通过 ref 访问）
```vue
<script setup lang="ts">
const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)
function exportCurrent() {
  // 使用 SuperTableCard 暴露的 queryParams 获取当前条件
  const params = { ...(superRef.value?.queryParams || {}) }
  // 调用你的后端导出接口
  // exportLoginLog(params)
}
</script>

<SuperTableCard ref="superRef" :columns="columns" :form-schema="formSchema" :query-api="listLoginLogPageable">
  <template #table-extra>
    <!-- 追加工具栏按钮：基于当前查询条件执行导出 -->
    <a-button type="default" @click="exportCurrent">导出当前条件</a-button>
  </template>
</SuperTableCard>
```

要点：
- `table-extra` 插槽没有作用域，但插槽内容处于父组件作用域，
  可通过父级 `ref` 读取 SuperTableCard 暴露的 `queryParams` 与方法。

### 4) bodyCell：自定义操作列与复杂渲染

实现说明（内部实现片段）：
```vue
<!-- SuperTableCard 内部将父级 #bodyCell 原样透传到 ProTableCard 的 a-table -->
<ProTableCard ...>
  <template #bodyCell="{ column, record, index }">
    <slot name="bodyCell" :column="column" :record="record" :index="index" />
  </template>
</ProTableCard>
```

最小示例：在列表中自定义“操作列”与“状态”渲染（含中文注释）
```vue
<script setup lang="ts">
import SuperTableCard from '~@/components/super-table-card/index.vue'
import { listUserPageable } from '~@/api/org/user'
import type { TableColumnType } from 'ant-design-vue'

// 列定义：为操作列设置唯一 key，便于插槽内识别
const columns: TableColumnType<any>[] = [
  { title: '用户名', dataIndex: 'username' },
  { title: '状态', dataIndex: 'status' },
  { title: '操作', key: 'action', width: 160 }, // 操作列通过插槽渲染
]

function onEdit(row: any) {
  // 中文注释：编辑当前行数据（自行实现具体逻辑）
}
function onDelete(row: any) {
  // 中文注释：删除当前行数据后记得刷新列表
}
</script>

<template>
  <SuperTableCard :columns="columns" :form-schema="formSchema" :query-api="listUserPageable">
    <!-- 自定义单元格内容：通过列的 key/dataIndex 匹配需要自定义的列 -->
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
      <!-- 其它列：保持默认渲染（不输出任何内容即可使用 a-table 默认单元格渲染） -->
    </template>
  </SuperTableCard>
}</template>
```

要点：
- 建议为“操作列”设置 `key: 'action'`，在插槽内通过 `column.key` 精准匹配。
- 其它列可通过 `column.dataIndex` 进行条件渲染；未匹配时不输出内容即可走默认渲染。
- SuperTableCard 会将 `#bodyCell` 作用域参数（`{ column, record, index }`）原样透传，无需重复定义。
- 保持插槽逻辑“只读 record、不随意修改对象结构”，避免影响表格内部 Diff 与性能。

## 选择事件透传与示例

- 事件：`selection-change`（当内部表格选择变化时触发）
  - 参数：`(selectedRowKeys: any[], selectedRows: any[])`
  - 选择依赖行主键：请通过 `tableProps` 为表格设置 `rowKey`（如 `id`）。

### 最小示例：页面监听选择变化
```vue
<script setup lang="ts">
import SuperTableCard from '~@/components/super-table-card/index.vue'
import { listLoginLogPageable } from '~@/api/audit-log/login'

const columns = [
  { title: '登录名', dataIndex: 'accountLoginName' },
  { title: '时间', dataIndex: 'operationTime' },
]

function onSelectionChange(selectedRowKeys: any[], selectedRows: any[]) {
  // 中文注释：拿到选中主键与行数据
  console.log('keys:', selectedRowKeys, 'rows:', selectedRows)
}
</script>

<template>
  <SuperTableCard
    :columns="columns"
    :form-schema="formSchema"
    :query-api="listLoginLogPageable"
    :table-props="{ rowKey: 'id' }"  <!-- 设置主键字段 -->
    :enable-row-selection="true"
    @selection-change="onSelectionChange"
  />
</template>
```

### 在父组件中持久化选择状态并批量操作
```vue
<script setup lang="ts">
const selectedKeys = ref<any[]>([])
const selectedRows = ref<any[]>([])

function onSelectionChange(keys: any[], rows: any[]) {
  // 中文注释：保存选择状态，配合工具栏或操作区进行批量动作
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
  <SuperTableCard
    :columns="columns"
    :form-schema="formSchema"
    :query-api="listLoginLogPageable"
    :table-props="{ rowKey: 'id' }"
    :enable-row-selection="true"
    @selection-change="onSelectionChange"
  >
    <template #table-extra>
      <!-- 将批量操作按钮追加到工具栏右侧 -->
      <a-button @click="exportSelected">导出选中</a-button>
    </template>
  </SuperTableCard>
</template>
```

### 高级：自定义选择行为（单选/禁用行）
> 通过 `ref` 访问子组件内部 ProTableCard 的选择配置并进行调整。

```vue
<script setup lang="ts">
const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)

onMounted(() => {
  const tableRef = (superRef.value as any)?.tableRef
  if (tableRef) {
    // 中文注释：将选择改为单选
    tableRef.state.rowSelections.type = 'radio'
    // 禁用条件：示例将 result 为 'success' 的行禁用
    tableRef.state.rowSelections.getCheckboxProps = (record: any) => ({
      disabled: record.result === 'success',
    })
  }
})
</script>

<template>
  <SuperTableCard
    ref="superRef"
    :columns="columns"
    :form-schema="formSchema"
    :query-api="listLoginLogPageable"
    :table-props="{ rowKey: 'id' }"
    :enable-row-selection="true"
    :row-selection-props="{ type: 'radio' }"
  />
</template>
```

注意事项：
- 事件在 `SuperTableCard -> ProTableCard -> a-table` 之间统一透传；监听 `@selection-change` 即可。
- 建议不要直接覆盖内部的选择 `onChange` 回调；如需扩展，请在父组件中使用事件或在 `ref.state.rowSelections` 上追加属性（勿替换）。
- 仅当 `enableRowSelection` 为 `true` 时才会展示选择列与触发事件；默认关闭以避免不必要的 UI 与状态。
