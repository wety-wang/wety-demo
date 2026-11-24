<script lang="ts" setup>
import type { TableColumnType } from 'ant-design-vue'
import type { PositionInfoVO } from '~@/api/org/position'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { message, Modal } from 'ant-design-vue'
import { deletePosition, listPositionPageable, updatePositionStatus } from '~@/api/org/position'
import SuperTableCard from '~@/components/super-table-card/index.vue'
import PositionEditModal from './components/position-edit-modal/index.vue'

// 状态选项
const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 },
]

// 表格列定义
const columns: TableColumnType<PositionInfoVO>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    align: 'center',
    customRender: ({ index }) => h('span', null, { default: () => index + 1 }),
    fixed: 'left',
  },
  {
    title: '岗位名称',
    dataIndex: 'name',
    key: 'name',
    width: 160,
    sorter: true,
  },
  {
    title: '岗位编码',
    dataIndex: 'postCode',
    key: 'postCode',
    width: 160,
    sorter: true,
  },
  {
    title: '排序',
    dataIndex: 'sortOrder',
    key: 'sortOrder',
    width: 90,
    align: 'center',
    sorter: true,
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 100,
    align: 'center',
  },
  {
    title: '备注',
    dataIndex: 'remark',
    key: 'remark',
    width: 220,
    ellipsis: true,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
  },
]

// 查询表单 Schema
const formSchema: FormItemSchema[] = [
  { name: 'name', label: '岗位名称', type: 'input', placeholder: '请输入岗位名称' },
  { name: 'postCode', label: '岗位编码', type: 'input', placeholder: '请输入岗位编码' },
  { name: 'status', label: '状态', type: 'select', placeholder: '请选择状态', options: statusOptions },
]

const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)
const editModalRef = ref<InstanceType<typeof PositionEditModal> | null>(null)

function openEdit(record?: PositionInfoVO | Record<string, any>) {
  editModalRef.value?.open(record as PositionInfoVO | undefined)
}

function handleToggleStatus(record: PositionInfoVO | Record<string, any>) {
  const pos = record as PositionInfoVO
  const targetStatus = pos.status === 1 ? 0 : 1
  const actionText = targetStatus === 1 ? '启用' : '停用'
  Modal.confirm({
    title: `确认${actionText}该岗位？`,
    content: `确定${actionText}岗位【${pos.name}】？`,
    async onOk() {
      await updatePositionStatus({ code: pos.code, status: targetStatus })
      message.success(`${actionText}成功`)
      superRef.value?.initQuery()
    },
  })
}

function handleDelete(record: PositionInfoVO | Record<string, any>) {
  const pos = record as PositionInfoVO
  Modal.confirm({
    title: '确认删除该岗位？',
    content: `确定删除岗位【${pos.name}】？`,
    async onOk() {
      await deletePosition({ code: pos.code })
      message.success('删除成功')
      superRef.value?.initQuery()
    },
  })
}
</script>

<template>
  <page-container>
    <SuperTableCard
      ref="superRef"
      title="岗位管理"
      :columns="columns"
      :form-schema="formSchema"
      :query-api="async (params) => await listPositionPageable(params)"
      :table-props="{
        rowKey: 'code',
        scroll: { x: 1000 },
        bordered: true,
      }"
    >
      <template #table-extra>
        <a-button type="primary" @click="openEdit()">
          新增岗位
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'success' : 'default'">
            {{ record.status === 1 ? '启用' : '停用' }}
          </a-tag>
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space :size="4">
            <a-button type="link" size="small" @click="openEdit(record)">
              修改
            </a-button>
            <a-button type="link" size="small" @click="handleToggleStatus(record)">
              {{ record.status === 1 ? '停用' : '启用' }}
            </a-button>
            <a-button type="link" size="small" danger @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </SuperTableCard>

    <PositionEditModal ref="editModalRef" @ok="superRef?.initQuery()" />
  </page-container>
</template>
