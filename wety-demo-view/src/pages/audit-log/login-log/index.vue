<script setup lang="ts">
import type { TableColumnType } from 'ant-design-vue'
import type { LoginLogInfoVO } from '~@/api/audit-log/login'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { Tag } from 'ant-design-vue'
import dayjs from 'dayjs'
import { h } from 'vue'
import { listLoginLogPageable } from '~@/api/audit-log/login'
import SuperTableCard from '~@/components/super-table-card/index.vue'

// 结果枚举映射
const ResultMap: Record<any, { label: string, value: string, color: string }> = {
  'success': { label: '成功', value: 'success', color: 'success' },
  'faild': { label: '失败', value: 'faild', color: 'error' },
}

// 列定义
const columns: TableColumnType<LoginLogInfoVO>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    customRender: ({ index }) => {
      return h('span', null, { default: () => index + 1 })
    },
    fixed: 'left',
  },
  { title: '登录名', dataIndex: 'accountLoginName', width: 100, sorter: true },
  { title: '用户名', dataIndex: 'accountName', width: 140, sorter: true },
  { title: '客户端IP', dataIndex: 'clientIp', width: 100 },
  {
    title: '结果',
    dataIndex: 'result',
    width: 100,
    customRender: ({ text }) => {
      const meta = ResultMap[text] ?? { label: String(text ?? ''), color: 'default' }
      return h(Tag, { color: meta.color }, { default: () => meta.label })
    },
  },
  {
    title: '登录时间',
    dataIndex: 'operationTime',
    width: 180,
    sorter: true,
    customRender: ({ text }) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : ''),
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true,
    width: 200,
    resizable: true,
  },
]

// SuperTableCard 查询表单 schema
const formSchema: FormItemSchema[] = [
  // 基础字段
  { name: 'accountLoginName', label: '登录名', type: 'input', placeholder: '输入登录名' },
  { name: 'accountName', label: '用户名', type: 'input', placeholder: '输入用户名' },
  { name: 'result', label: '结果', type: 'select', placeholder: '请选择结果', options: Object.values(ResultMap) },
  { name: 'clientIp', label: '客户端IP', type: 'input', placeholder: '输入IP' },
  // 高级字段
  { name: 'operationTime', label: '登录时间', type: 'daterange', advanced: true },
]
</script>

<template>
  <page-container>
    <SuperTableCard
      title="登录日志"
      :columns="columns"
      :form-schema="formSchema"
      :query-api="async (params) => await listLoginLogPageable(params)"
      :table-props="{
        scroll: { x: 1000 },
        bordered: true,
      }"
    />
  </page-container>
</template>
