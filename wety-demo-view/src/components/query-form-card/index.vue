<script setup lang="ts">
import type { FormInstance } from 'ant-design-vue'
import type { FormItemSchema } from './schema'
// 通用查询条件组件：用于表格或其他数据查询场景
// 提供：
// - 基础/高级查询字段渲染
// - 必填与规则校验
// - 查询/重置按钮与事件
// - 自定义字段插槽 form-extra 与操作插槽 query-actions
import { DownOutlined, UpOutlined } from '@ant-design/icons-vue'

interface Props {
  // 查询表单 schema
  formSchema: FormItemSchema[]
  // v-model 绑定的查询参数对象
  modelValue: Record<string, any>
  // 是否默认展开高级查询项
  defaultExpand?: boolean
  // 标签栅格（可选，默认 { span: 7 }）
  labelCol?: { span?: number, offset?: number }
}

const props = withDefaults(defineProps<Props>(), {
  defaultExpand: false,
  labelCol: () => ({ span: 7 }),
})

// 事件：提交查询、重置，以及 v-model
const emit = defineEmits<{
  (e: 'submit', params: Record<string, any>): void
  (e: 'reset', params: Record<string, any>): void
  (e: 'update:modelValue', params: Record<string, any>): void
}>()

// 展开/收起高级查询项
const expand = ref<boolean>(props.defaultExpand)
function expandChange() {
  expand.value = !expand.value
}

// 计算是否存在高级查询项：
// 1) 来自 schema 的高级字段（i.advanced 为真）
// 2) 来自插槽 form-advanced-extra（父组件可能追加高级自定义字段）
const slots = useSlots()
const hasAdvancedSchema = computed(() => (props.formSchema || []).some(i => !!(i as any)?.advanced))
const hasAdvancedSlot = computed(() => !!slots['form-advanced-extra'])
const hasAdvanced = computed(() => hasAdvancedSchema.value || hasAdvancedSlot.value)

// 表单引用：用于执行整表单校验与重置
const formRef = ref<FormInstance | null>(null)

// 内部查询参数：使用外部 v-model 的对象引用，保持双向绑定
const queryParams = reactive<Record<string, any>>({})
Object.assign(queryParams, props.modelValue || {})

// 同步外部 v-model 变化（若父级替换对象）
watch(
  () => props.modelValue,
  (val) => {
    Object.assign(queryParams, val || {})
  },
  { deep: true },
)

// 供插槽与外部调用的便捷 setter
function setQueryParam(name: string, value: any) {
  queryParams[name] = value
  emit('update:modelValue', { ...queryParams })
}

// 查询：校验通过后触发 submit 事件
async function doSubmit() {
  try {
    await formRef.value?.validate()
    // 在提交前对 select 与 treeSelect 的多选值进行规范化：
    // - select: 当开启多选时，确保传递数组
    // - treeSelect: 多选时根据 valueStrategy（默认仅叶子）过滤父级
    const normalized: Record<string, any> = { ...queryParams }
    const parentsMap: Record<string, Set<any>> = {}
    for (const item of (props.formSchema || [])) {
      const name = item.name
      if (item.type === 'select') {
        // a-select 多选：mode === 'multiple' 时保证是数组
        const isMultiple = item.componentProps?.mode === 'multiple'
        const val = normalized[name]
        if (isMultiple) {
          if (val == null)
            normalized[name] = []
          else if (!Array.isArray(val)
          )
            normalized[name] = [val]
        }
      }
      if (item.type === 'treeSelect') {
        // a-tree-select 多选：multiple 或 treeCheckable 任一为真视为多选
        const isMultiple = !!(item.componentProps?.multiple || item.componentProps?.treeCheckable)
        const val = normalized[name]
        // 预构建父节点集合用于叶子过滤
        if (!parentsMap[name]) {
          const parents = new Set<any>()
          const traverse = (nodes: any[]) => {
            if (!nodes) {
              return
            }
            for (const n of nodes) {
              if (n && Array.isArray(n.children) && n.children.length) {
                parents.add(n.value)
                traverse(n.children)
              }
            }
          }
          traverse(item.componentProps?.treeData ?? item.options ?? [])
          parentsMap[name] = parents
        }
        const parents = parentsMap[name]
        // 归一化为数组（兼容 labelInValue）
        if (isMultiple) {
          const arr = val == null ? [] : (Array.isArray(val) ? val : [val])
          const isLabelInValue = !!item.componentProps?.labelInValue
          if (item.valueStrategy !== 'all') {
            // 默认仅传递叶子节点：过滤掉父级值
            if (isLabelInValue) {
              normalized[name] = arr.filter((it: any) => !parents.has(it?.value))
            }
            else {
              normalized[name] = arr.filter((v: any) => !parents.has(v))
            }
          }
          else {
            normalized[name] = arr
          }
        }
      }
    }
    emit('submit', normalized)
  }
  catch (e) {
    // 校验失败时不触发
  }
}

// 重置：清空 schema 内字段并重置校验，再触发 reset 事件
function doReset() {
  for (const item of props.formSchema) {
    queryParams[item.name] = undefined
  }
  formRef.value?.resetFields()
  emit('update:modelValue', { ...queryParams })
  emit('reset', { ...queryParams })
}

// 暴露能力：供父组件直接控制
defineExpose({
  validate: () => formRef.value?.validate(),
  reset: () => doReset(),
  setParam: setQueryParam,
  queryParams,
})

// Select：支持通过 componentProps.searchFields 指定匹配字段，默认 ['label']
function defaultSelectFilterOption(input: string, option: any, fields?: string[]) {
  // 优先按自定义字段匹配，若自定义只写了 label 但数据是 title，可自行把 option 改为包含 label 或在 options 构造时映射
  if (includesByFields(input, option, fields)) {
    return true
  }
  // 兼容兜底：若未匹配且存在 title 字段，尝试按 title 匹配，避免旧数据仅有 title 时失效
  const text = (`${option?.title ?? ''}`).toLowerCase()
  return text.includes((input ?? '').toLowerCase())
}

// TreeSelect：支持通过 componentProps.searchFields 指定匹配字段，默认 ['label']
// 同时考虑 fieldNames 映射：当字段数组包含 'label'，会映射到 fieldNames.label 或默认 'title'
function defaultTreeFilterNode(input: string, treeNode: any, fields?: string[], fieldNames?: { label?: string, value?: string, children?: string }) {
  // 将 'label' 映射为真实节点字段名（默认 antd 的 label 对应 'title'）
  const normalizedFields = (fields && fields.length ? fields : ['label']).map(f =>
    f === 'label' ? (fieldNames?.label ?? 'title') : f,
  )
  if (includesByFields(input, treeNode, normalizedFields)) {
    return true
  }
  // 兼容兜底：若未匹配且存在 title 字段，尝试按 title 匹配
  const text = (`${treeNode?.title ?? ''}`).toLowerCase()
  return text.includes((input ?? '').toLowerCase())
}

// 通用：按给定字段数组进行大小写不敏感匹配
// obj：候选对象（Select 的 option 或 TreeSelect 的 treeNode）
// fields：要匹配的字段名数组；当为空时默认使用 ['label']
function includesByFields(input: string, obj: any, fields?: string[]) {
  const keywords = (input ?? '').toLowerCase()
  const keys = (fields && fields.length ? fields : ['label'])
  for (const key of keys) {
    const val = obj?.[key]
    if (val != null && String(val).toLowerCase().includes(keywords)) {
      return true
    }
  }
  return false
}
</script>

<template>
  <a-card mb-4>
    <a-form ref="formRef" :label-col="props.labelCol" :model="queryParams">
      <!-- 基础字段：非高级项 -->
      <a-row :gutter="[15, 0]">
        <template v-for="item in props.formSchema.filter(i => !i.advanced)" :key="item.name">
          <a-col :span="item.span ?? 6">
            <a-form-item
              :name="item.name"
              :label="item.label"
              :rules="item.rules ?? (item.required ? [{ required: true, message: `${item.label}为必填项` }] : undefined)"
            >
              <!-- 输入框 -->
              <a-input
                v-if="(item.type ?? 'input') === 'input'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请输入${item.label}`)"
                v-bind="item.componentProps || {}"
              />
              <!-- 下拉选择（支持单选/多选，通过 componentProps.mode 控制） -->
              <a-select
                v-else-if="item.type === 'select'"
                v-model:value="queryParams[item.name]"
                :options="item.options || []"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-search="item.componentProps?.showSearch ?? true"
                :filter-option="item.componentProps?.filterOption ?? ((input: string, option: any) => defaultSelectFilterOption(input, option, item.componentProps?.searchFields))"
                v-bind="item.componentProps || {}"
              />
              <!-- 树形下拉选择（单选/多选可配置；多选时值为数组） -->
              <a-tree-select
                v-else-if="item.type === 'treeSelect'"
                v-model:value="queryParams[item.name]"
                :tree-data="item.componentProps?.treeData ?? item.options ?? []"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-search="item.componentProps?.showSearch ?? true"
                :filter-tree-node="item.componentProps?.filterTreeNode ?? ((input: string, node: any) => defaultTreeFilterNode(input, node, item.componentProps?.searchFields, item.componentProps?.fieldNames))"
                v-bind="item.componentProps || {}"
              />
              <!-- 单选 -->
              <a-radio-group
                v-else-if="item.type === 'radio'"
                v-model:value="queryParams[item.name]"
                v-bind="item.componentProps || {}"
              >
                <a-radio v-for="opt in (item.options || [])" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </a-radio>
              </a-radio-group>
              <!-- 多选（复选框组） -->
              <a-checkbox-group
                v-else-if="item.type === 'checkbox'"
                v-model:value="queryParams[item.name]"
                :options="item.options || []"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期选择（用 valueFormat 输出字符串，避免传 dayjs 对象） -->
              <a-date-picker
                v-else-if="item.type === 'date'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD'"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期范围选择（建议使用 valueFormat 输出字符串） -->
              <a-range-picker
                v-else-if="item.type === 'daterange'"
                v-model:value="queryParams[item.name]"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD'"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期时间选择：默认输出 YYYY-MM-DD HH:mm:ss 字符串 -->
              <a-date-picker
                v-else-if="item.type === 'datetime'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-time="item.componentProps?.showTime ?? true"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD HH:mm:ss'"
                v-bind="item.componentProps || {}"
              />
              <!-- 时间选择：默认输出 HH:mm:ss 字符串 -->
              <a-time-picker
                v-else-if="item.type === 'time'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :value-format="item.componentProps?.valueFormat ?? 'HH:mm:ss'"
                v-bind="item.componentProps || {}"
              />
            </a-form-item>
          </a-col>
        </template>
        <!-- 自定义表单字段插槽（基础区域） -->
        <slot name="form-extra" :query-params="queryParams" :set-param="setQueryParam" />
      </a-row>

      <!-- 高级字段：仅当存在高级项/插槽且处于展开态时显示 -->
      <a-row v-if="hasAdvanced && expand" :gutter="[15, 0]">
        <template v-for="item in props.formSchema.filter(i => i.advanced)" :key="item.name">
          <a-col :span="item.span ?? 6">
            <a-form-item
              :name="item.name"
              :label="item.label"
              :rules="item.rules ?? (item.required ? [{ required: true, message: `${item.label}为必填项` }] : undefined)"
            >
              <!-- 输入框 -->
              <a-input
                v-if="(item.type ?? 'input') === 'input'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请输入${item.label}`)"
                v-bind="item.componentProps || {}"
              />
              <!-- 下拉选择 -->
              <a-select
                v-else-if="item.type === 'select'"
                v-model:value="queryParams[item.name]"
                :options="item.options || []"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-search="item.componentProps?.showSearch ?? true"
                :filter-option="item.componentProps?.filterOption ?? ((input: string, option: any) => defaultSelectFilterOption(input, option, item.componentProps?.searchFields))"
                v-bind="item.componentProps || {}"
              />
              <!-- 树形下拉选择（高级区） -->
              <a-tree-select
                v-else-if="item.type === 'treeSelect'"
                v-model:value="queryParams[item.name]"
                :tree-data="item.componentProps?.treeData ?? item.options ?? []"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-search="item.componentProps?.showSearch ?? true"
                :filter-tree-node="item.componentProps?.filterTreeNode ?? ((input: string, node: any) => defaultTreeFilterNode(input, node, item.componentProps?.searchFields, item.componentProps?.fieldNames))"
                v-bind="item.componentProps || {}"
              />
              <!-- 单选 -->
              <a-radio-group
                v-else-if="item.type === 'radio'"
                v-model:value="queryParams[item.name]"
                v-bind="item.componentProps || {}"
              >
                <a-radio v-for="opt in (item.options || [])" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </a-radio>
              </a-radio-group>
              <!-- 多选（复选框组） -->
              <a-checkbox-group
                v-else-if="item.type === 'checkbox'"
                v-model:value="queryParams[item.name]"
                :options="item.options || []"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期选择 -->
              <a-date-picker
                v-else-if="item.type === 'date'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD'"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期范围选择 -->
              <a-range-picker
                v-else-if="item.type === 'daterange'"
                v-model:value="queryParams[item.name]"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD'"
                v-bind="item.componentProps || {}"
              />
              <!-- 日期时间选择（高级区）：默认输出 YYYY-MM-DD HH:mm:ss -->
              <a-date-picker
                v-else-if="item.type === 'datetime'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :show-time="item.componentProps?.showTime ?? true"
                :value-format="item.componentProps?.valueFormat ?? 'YYYY-MM-DD HH:mm:ss'"
                v-bind="item.componentProps || {}"
              />
              <!-- 时间选择（高级区）：默认输出 HH:mm:ss -->
              <a-time-picker
                v-else-if="item.type === 'time'"
                v-model:value="queryParams[item.name]"
                :placeholder="item.placeholder ?? (`请选择${item.label}`)"
                :value-format="item.componentProps?.valueFormat ?? 'HH:mm:ss'"
                v-bind="item.componentProps || {}"
              />
            </a-form-item>
          </a-col>
        </template>
        <!-- 自定义表单字段插槽（高级区域） -->
        <slot name="form-advanced-extra" :query-params="queryParams" :set-param="setQueryParam" />
      </a-row>

      <!-- 查询操作按钮 -->
      <a-row :span="24" style="text-align: right">
        <a-col :span="24">
          <a-space flex justify-end w-full>
            <!-- 当不存在高级字段与高级插槽时，不显示展开/收起按钮 -->
            <a-button v-if="hasAdvanced" type="link" @click="expandChange">
              {{ expand ? '收起' : '展开' }}
              <UpOutlined v-if="expand" />
              <DownOutlined v-else />
            </a-button>
            <a-button type="primary" @click="doSubmit">
              查询
            </a-button>
            <a-button @click="doReset">
              重置
            </a-button>
            <!-- 查询操作扩展插槽 -->
            <slot name="query-actions" :query-params="queryParams" />
          </a-space>
        </a-col>
      </a-row>
    </a-form>
  </a-card>
</template>

<style scoped>
/* 轻量样式占位，可根据全局主题调整 */
</style>
