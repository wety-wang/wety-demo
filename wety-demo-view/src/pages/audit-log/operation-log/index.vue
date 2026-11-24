<script lang="ts" setup>
import type { TableColumnType } from 'ant-design-vue'
import type { OperationLogInfoVO, PermissionHolder } from '~@/api/audit-log/operation'
import type { CodeNameDTO } from '~@/api/constant'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { Tag } from 'ant-design-vue'
import dayjs from 'dayjs'
import { h } from 'vue'
import { listAllUser, listOperationLogPageable, queryModuleTree } from '~@/api/audit-log/operation'
import SuperTableCard from '~@/components/super-table-card/index.vue'

// 操作类型枚举映射
const OperationTypeMap: Record<any, { label: string, value: string }> = {
  'create': { label: '新增', value: 'create' },
  'update': { label: '修改', value: 'update' },
  'delete': { label: '删除', value: 'delete' },
  'query': { label: '查询', value: 'query' },
}

// 操作结果枚举映射
const OperationResultMap: Record<any, { label: string, value: string, color: string }> = {
  'success': { label: '成功', value: 'success', color: 'success' },
  'faild': { label: '失败', value: 'faild', color: 'error' },
}

const columns: TableColumnType<OperationLogInfoVO>[] = [
  { title: '序号', dataIndex: 'index', width: 80, customRender: ({ index }) => h('span', null, { default: () => index + 1 }) },
  { title: '模块', dataIndex: 'modelName', width: 120, key: 'modelName' },
  { title: '操作人', dataIndex: 'accountCode', width: 180, key: 'accountCode', customRender: ({ record }) => h('span', null, { default: () => `${record.accountName}(${record.accountLoginName})` }) },
  { title: '所属部门', dataIndex: 'deptName', width: 140, key: 'deptName', customRender: ({ text }) => text || '-' },
  { title: '客户端IP', dataIndex: 'clientIp', width: 120, key: 'clientIp' },
  {
    title: '操作类型',
    dataIndex: 'operationType',
    width: 100,
    key: 'operationType',
    customRender: ({ text }) => {
      const meta = OperationTypeMap[text] ?? { label: String(text ?? ''), color: 'default' }
      return h('span', null, { default: () => meta.label })
    },
  },
  {
    title: '操作时间',
    dataIndex: 'operationTime',
    width: 180,
    key: 'operationTime',
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : ''),
  },
  {
    title: '操作结果',
    dataIndex: 'result',
    width: 100,
    key: 'result',
    customRender: ({ text }) => {
      const meta = OperationResultMap[text] ?? { label: String(text ?? ''), color: 'default' }
      return h(Tag, { color: meta.color }, { default: () => meta.label })
    },
  },
  { title: '描述', dataIndex: 'description', width: 520, key: 'description' },
  { title: '耗时(ms)', dataIndex: 'durationMs', width: 100, key: 'durationMs' },
]

const formSchema = ref<FormItemSchema[]>([
  { name: 'modelCodes', label: '模块', type: 'treeSelect', placeholder: '选择模块', valueStrategy: 'leaf', componentProps: { allowClear: true, showSearch: true, treeCheckable: true, multiple: true, maxTagCount: 2 } },
  { name: 'accountCodes', label: '用户', type: 'select', placeholder: '选择用户', options: [], componentProps: { mode: 'multiple', allowClear: true, showSearch: true, maxTagCount: 2 } },
  { name: 'operationTypes', label: '操作类型', type: 'select', placeholder: '选择操作类型', options: Object.values(OperationTypeMap), componentProps: { mode: 'multiple', allowClear: true, maxTagCount: 2 } },
  { name: 'result', label: '结果', type: 'select', placeholder: '请选择结果', options: Object.values(OperationResultMap) },
  // 高级字段
  { name: 'operationTime', label: '操作时间', type: 'daterange', advanced: true, componentProps: { valueFormat: 'YYYY-MM-DD' } },
  { name: 'clientIp', label: '客户端IP', type: 'input', placeholder: '输入IP', advanced: true },
])

// 将后端返回的权限树转换为 TreeSelect 可用的树形数据
function transformModuleTreeToTreeData(tree: PermissionHolder[] | undefined): Array<{ title: string, value: string, children?: any[] }> {
  if (!tree || !tree.length)
    return []
  // 仅保留 showTreeNode !== 'no' 的节点（后端控制前端展示）
  return tree.filter(node => !node.showTreeNode || node.showTreeNode === 'yes')
    .map((node) => {
      const item: any = { title: node.name, value: node.code }
      if (node.children && node.children.length)
        item.children = transformModuleTreeToTreeData(node.children)
      return item
    })
}

// 初始化模块选项
onMounted(async () => {
  try {
    const tree = await queryModuleTree()
    const treeData = transformModuleTreeToTreeData(tree)
    const item = formSchema.value.find(i => i.name === 'modelCodes')
    // 将树形数据设置到 componentProps.treeData；QueryFormCard 会优先使用此字段
    if (item) {
      item.componentProps = {
        ...(item.componentProps || {}),
        treeData,
      }
    }
  }
  catch (e) {
    // 忽略错误，页面仍可查询其他条件
  }
})

// 初始化用户下拉选项：查询所有用户并转换为 select options
onMounted(async () => {
  try {
    // 获取后端返回的 CodeNameDTO 列表
    const users: CodeNameDTO[] = await listAllUser()
    // 转换为 a-select 可用的 { label, value }
    const opts = (users || []).map(u => ({ label: u.name, value: u.code }))
    const userItem = formSchema.value.find(i => i.name === 'accountCodes')
    if (userItem)
      userItem.options = opts
  }
  catch (e) {
    // 忽略错误，保留空选项，不影响其他查询条件
  }
})
</script>

<template>
  <page-container>
    <SuperTableCard
      title="操作日志"
      :columns="columns"
      :form-schema="formSchema"
      :query-api="async (params) => await listOperationLogPageable(params)"
      :table-props="{
        scroll: { x: 1500 },
        bordered: true,
      }"
    />
  </page-container>
</template>
