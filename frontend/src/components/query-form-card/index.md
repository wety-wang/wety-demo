# QueryFormCard 使用说明

QueryFormCard 是一个通用查询表单组件，支持基础/高级查询项、必填与规则校验、查询/重置按钮与自定义插槽。它通过 `v-model` 与外部对象双向绑定，并在提交时抛出完整查询参数。

## 组件定位
- 渲染表单字段（基础/高级）、处理校验与查询/重置交互
- 以 `FormItemSchema[]` 描述字段结构，实现快速配置化
- 提供插槽以拓展自定义字段与操作区

## 快速开始（最小用法）
```vue
<script setup lang="ts">
import QueryFormCard from '~@/components/query-form-card/index.vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

const formSchema: FormItemSchema[] = [
  { name: 'accountLoginName', label: '登录名', type: 'input', required: true },
  { name: 'result', label: '结果', type: 'select', advanced: true, options: [
    { label: '成功', value: 'success' }, { label: '失败', value: 'faild' }
  ] },
]
const queryParams = reactive<Record<string, any>>({})

function handleSubmit(params: Record<string, any>) { /* 接收完整查询条件 */ }
function handleReset(params: Record<string, any>) { /* 重置后的条件 */ }
</script>

<template>
  <QueryFormCard
    :form-schema="formSchema"
    :model-value="queryParams"
    @update:modelValue="(v) => Object.assign(queryParams, v || {})"
    @submit="handleSubmit"
    @reset="handleReset"
  />
  <!-- 展开/收起由组件内部按钮控制，也可通过 defaultExpand 默认展开 -->
</template>
```

## Props
- `formSchema: FormItemSchema[]` 字段配置数组（见下文“Schema 字段说明”）。
- `modelValue: Record<string, any>` 外部查询对象（使用对象引用实现双向绑定）。
- `defaultExpand?: boolean` 是否默认展开高级查询项，默认 `false`。
- `labelCol?: { span?: number; offset?: number }` 标签栅格，默认 `{ span: 7 }`。

## 事件
- `submit(params: Record<string, any>)` 点击“查询”且校验通过时触发，参数为“完整查询条件”。
- `reset(params: Record<string, any>)` 点击“重置”后触发，参数为重置后的查询条件。
- `update:modelValue(params: Record<string, any>)` 当内部变更查询对象时抛出（保持外部 v-model 同步）。

## 暴露方法
- `validate()` 触发表单校验（返回 Promise）。
- `reset()` 重置表单（清空 schema 中字段并清理校验状态）。
- `setParam(name: string, value: any)` 便捷写入查询对象（供插槽与外部调用）。
- `queryParams` 当前内部查询对象引用（常用于插槽访问）。

## Schema 字段说明（FormItemSchema）
- 基础键：
  - `name: string` 字段名（需与后端参数名一致）。
  - `label: string` 标签文本。
  - `type?: 'input' | 'select' | 'radio' | 'checkbox' | 'date' | 'daterange' | 'datetime' | 'time' | 'treeSelect'` 组件类型，默认 `input`。
  - `advanced?: boolean` 是否为高级查询项（收起时隐藏）。
  - `span?: number` 列栅格宽度，默认 `8`。
  - `placeholder?: string` 占位提示。
  - `required?: boolean` 是否必填（默认根据 `rules` 推断）。
  - `rules?: any[]` 校验规则数组（优先级高于 `required`）。
- 选择型：
  - `options?: { label: string; value: any; children?: any[] }[]` 选项集（select/radio/checkbox）。对于 `treeSelect`，`options` 可包含 `children` 构造树形结构。
- 组件属性：
  - `componentProps?: Record<string, any>` 透传给具体表单组件（如 `a-select` 的 `mode`、`a-date-picker` 的 `valueFormat` 等）。
  - `treeSelect` 支持通过 `componentProps.treeData` 传入树数据（优先级高于 `options`），默认字段名 `{ title, value, children }`；若后端返回字段不同，请使用 `componentProps.fieldNames` 进行映射。
  - 搜索字段（Select/TreeSelect 通用）：
    - `componentProps.searchFields?: string[]` 指定用于搜索匹配的字段名数组，大小写不敏感；不配置时默认 `['label']`。
    - TreeSelect 当 `searchFields` 包含 `'label'` 时，会按 `fieldNames.label` 指定的字段进行匹配；未配置 `fieldNames` 时，`label` 将映射为默认的 `title`。
    - 兼容兜底：若未命中且存在 `title` 字段，会进行兜底匹配，避免旧数据仅含 `title` 时失效。
 - 值传递策略：
  - `valueStrategy?: 'leaf' | 'all'`（仅对 `treeSelect` 生效），控制多选时的值归一化。`leaf`（默认）仅传叶子节点值；`all` 保留所有选中节点（包含父级）。

## 插槽
- `#form-extra` 基础区域追加自定义字段。
- `#form-advanced-extra` 高级区域追加自定义字段。
- `#query-actions` 操作区追加按钮或说明。

示例：高级区域增加“部门”字段（含中文注释）
```vue
<QueryFormCard :form-schema="formSchema" :model-value="queryParams">
  <template #form-advanced-extra="{ queryParams, setParam }">
    <!-- 自定义字段：部门，使用 setParam 写入到查询对象 -->
    <a-form-item name="departmentId" label="部门">
      <a-select :options="deptOptions" :value="queryParams.departmentId" @change="v => setParam('departmentId', v)" />
    </a-form-item>
  </template>
</QueryFormCard>
```

## 交互与校验说明
- 点击“查询”：内部调用 `formRef.validate()` 校验所有可见项，校验通过触发 `submit` 并携带完整条件。
- 点击“重置”：将 `formSchema` 中的字段重置为 `undefined`，清理校验状态，同时触发 `update:modelValue` 与 `reset`。
- 展开/收起：点击操作区的“展开/收起”按钮切换高级区域渲染。

## 注意事项
- 日期/范围/日期时间/时间默认通过 `valueFormat` 输出字符串，避免向后端传 `dayjs` 实例。
- `modelValue` 推荐始终为同一个对象引用，以便父级与子级之间高效同步。
- 必填与规则：若指定 `rules`，组件将优先采用规则数组进行校验与提示。

## 日期与时间类型（格式与示例）

- 默认格式说明（可通过 `componentProps.valueFormat` 覆盖）：
  - `date`：`YYYY-MM-DD`
  - `daterange`：`YYYY-MM-DD`（返回 `[string, string]`）
  - `datetime`：`YYYY-MM-DD HH:mm:ss`（启用 `showTime`，返回字符串）
  - `time`：`HH:mm:ss`

### 最小示例：标准日期/范围
```vue
<script setup lang="ts">
import QueryFormCard from '~@/components/query-form-card/index.vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

const formSchema: FormItemSchema[] = [
  { name: 'date', label: '日期', type: 'date' },
  { name: 'range', label: '范围', type: 'daterange' },
]
const queryParams = reactive<Record<string, any>>({})
</script>

<template>
  <QueryFormCard :form-schema="formSchema" :model-value="queryParams" @submit="p => console.log(p)" />
</template>
```

### 日期时间与独立时间（含中文注释）
```vue
<script setup lang="ts">
import QueryFormCard from '~@/components/query-form-card/index.vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

// 通过 componentProps.valueFormat 可覆盖默认输出格式
const formSchema: FormItemSchema[] = [
  { name: 'createdAt', label: '创建时间', type: 'datetime', componentProps: { /* showTime 默认开启 */ } },
  { name: 'execAt', label: '执行时间', type: 'time' },
]
const queryParams = reactive<Record<string, any>>({})
function onSubmit(params: Record<string, any>) {
  // params.createdAt 为 'YYYY-MM-DD HH:mm:ss' 字符串
  // params.execAt 为 'HH:mm:ss' 字符串
}
</script>

<template>
  <QueryFormCard :form-schema="formSchema" :model-value="queryParams" @submit="onSubmit" />
</template>
```

### 自定义格式覆盖（高级用法）
```vue
const formSchema: FormItemSchema[] = [
  // 自定义日期时间格式为毫秒时间戳（字符串），并关闭秒
  { name: 'dt', label: '日期时间', type: 'datetime', componentProps: { valueFormat: 'YYYY-MM-DD HH:mm', showTime: true } },
  // 日期范围自定义为 YYYY/MM/DD
  { name: 'dr', label: '范围', type: 'daterange', advanced: true, componentProps: { valueFormat: 'YYYY/MM/DD' } },
]
```

## 复杂插槽示例与说明

### 1) form-advanced-extra：多字段联动 + 远端搜索（防抖）
```vue
<script setup lang="ts">
// 远端搜索（客户端类型）示例：输入关键字后 300ms 防抖触发
const clientTypeOptions = ref<{ label: string; value: string }[]>([])
let timer: any = null
function searchClientTypes(keyword: string) {
  clearTimeout(timer)
  timer = setTimeout(async () => {
    // 这里调用后端接口：/api/clientType/search?q=keyword
    clientTypeOptions.value = [
      { label: `${keyword}-类型A`, value: 'A' },
      { label: `${keyword}-类型B`, value: 'B' },
    ]
  }, 300)
}
</script>

<QueryFormCard :form-schema="formSchema" :model-value="queryParams">
  <template #form-advanced-extra="{ queryParams, setParam }">
    <!-- 联动：选择客户端类型后，自动清空“用户名”以减小干扰 -->
    <a-form-item name="clientType" label="客户端类型">
      <a-select
        :options="clientTypeOptions"
        :value="queryParams.clientType"
        show-search
        :filter-option="false"
        @search="searchClientTypes"
        @change="(v) => { setParam('clientType', v); setParam('accountName', undefined) }"
      />
    </a-form-item>
    <!-- 关联字段：用户名（受 clientType 影响） -->
    <a-form-item name="accountName" label="用户名">
      <a-input v-model:value="queryParams.accountName" placeholder="输入用户名" />
    </a-form-item>
  </template>
</QueryFormCard>
```

要点：
- 使用 `show-search + filter-option=false + @search` 实现远端检索。
- 防抖处理避免过多请求；联动重置减少“脏数据”。

### 2) query-actions：一键清空高级字段 + 触发查询
```vue
<QueryFormCard :form-schema="formSchema" :model-value="queryParams" @submit="onSubmit">
  <template #query-actions="{ queryParams }">
    <!-- 清空所有高级字段（示例：clientType、result 等） -->
    <a-button type="link" @click="() => { queryParams.clientType = undefined; queryParams.result = undefined }">清空高级条件</a-button>
    <!-- 直接触发查询（需在父级实现 onSubmit） -->
    <a-button type="primary" @click="() => onSubmit({ ...queryParams })">立即查询</a-button>
  </template>
</QueryFormCard>
```

要点：
- 操作区适合追加“快捷操作”，同时支持直接触发查询事件。

## 树形选择与多选（示例与说明）

### 1) TreeSelect 单选（默认只传选中值）
```vue
<script setup lang="ts">
import QueryFormCard from '~@/components/query-form-card/index.vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

const formSchema: FormItemSchema[] = [
  // 单选：选择模块
  {
    name: 'modelCode', label: '模块', type: 'treeSelect',
    // 树数据可放在 options（支持 children）或 componentProps.treeData
    options: [
      { label: '审计', value: 'audit', children: [
        { label: '登录日志', value: 'audit-login' },
        { label: '操作日志', value: 'audit-operation' },
      ] },
    ],
    componentProps: { allowClear: true, showSearch: true },
  },
]
const queryParams = reactive<Record<string, any>>({})
</script>

<template>
  <QueryFormCard :form-schema="formSchema" :model-value="queryParams" @submit="p => console.log(p)" />
</template>
```

### 2) TreeSelect 多选（默认仅传叶子，支持包含父级）
```vue
<script setup lang="ts">
import QueryFormCard from '~@/components/query-form-card/index.vue'
import type { FormItemSchema } from '~@/components/query-form-card/schema'

const formSchema: FormItemSchema[] = [
  {
    name: 'modelCodes', label: '模块', type: 'treeSelect',
    // 默认 leaf：仅传叶子节点；如需同时传父级，设为 'all'
    valueStrategy: 'leaf',
    componentProps: {
      treeCheckable: true,  // 开启复选框多选
      multiple: true,       // 或仅使用 treeCheckable 亦可
      allowClear: true,
      showSearch: true,
      // 若后端返回字段不是 { title, value, children }，可用 fieldNames 映射：
      // fieldNames: { label: 'name', value: 'code', children: 'subItems' },
    },
    // 树数据可通过 componentProps.treeData 或 options.children 提供
    options: [
      { label: '审计', value: 'audit', children: [
        { label: '登录日志', value: 'audit-login' },
        { label: '操作日志', value: 'audit-operation' },
      ] },
    ],
  },
]
const queryParams = reactive<Record<string, any>>({})
function onSubmit(params: Record<string, any>) {
  // 默认仅包含叶子节点；若设 valueStrategy='all'，则包含父级
  console.log('提交参数：', params.modelCodes)
}
</script>

<template>
  <QueryFormCard :form-schema="formSchema" :model-value="queryParams" @submit="onSubmit" />
</template>
```

### 3) Select 多选（数组值归一化）
```vue
const formSchema: FormItemSchema[] = [
  {
    name: 'roles', label: '角色', type: 'select',
    options: [ { label: '管理员', value: 'ADMIN' }, { label: '用户', value: 'USER' } ],
    componentProps: { mode: 'multiple', allowClear: true, searchFields: ['label'] },
  },
]
// 说明：当 mode='multiple' 时，提交会确保值为数组；空值时为 []
```

## 搜索与匹配（searchFields）

### Select：自定义搜索字段示例
```vue
const formSchema: FormItemSchema[] = [
  {
    name: 'clientType', label: '客户端类型', type: 'select',
    options: [
      { label: '安卓', value: 'android', pinyin: 'anzhuo' },
      { label: 'iOS', value: 'ios', pinyin: 'pingguo' },
    ],
    componentProps: {
      showSearch: true,
      // 默认 ['label']，这里扩展到多个字段匹配
      searchFields: ['label', 'value', 'pinyin'],
    },
  },
]
```

### TreeSelect：结合 fieldNames 的搜索字段示例
```vue
const formSchema: FormItemSchema[] = [
  {
    name: 'deptId', label: '部门', type: 'treeSelect',
    valueStrategy: 'leaf',
    componentProps: {
      showSearch: true,
      fieldNames: { label: 'name', value: 'id', children: 'nodes' },
      // 当 searchFields 包含 'label'，会映射为 fieldNames.label，即 'name'
      searchFields: ['label', 'code'], // 这里等价于 ['name', 'code']
      treeData: [
        { name: '总部', id: 1, code: 'HQ', nodes: [
          { name: '技术部', id: 2, code: 'TECH' },
          { name: '运营部', id: 3, code: 'OPS' },
        ] },
      ],
    },
  },
]
```

说明：
- 默认搜索字段为 `['label']`；TreeSelect 中若未配置 `fieldNames`，`label` 将映射为 `title`。
- 为兼容旧数据，未命中时会尝试 `title` 兜底匹配（若存在）。
- 如需完全自定义过滤逻辑，可使用：
  - Select：`componentProps.filterOption?: (input, option) => boolean`
  - TreeSelect：`componentProps.filterTreeNode?: (input, treeNode) => boolean`
