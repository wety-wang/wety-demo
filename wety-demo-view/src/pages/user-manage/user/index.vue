<script lang="ts" setup>
import type { TableColumnType } from 'ant-design-vue'
import type { AccountInfoVO } from '~@/api/user-manage/user'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { message, Modal } from 'ant-design-vue'
import { deleteUser, listUserPageable } from '~@/api/user-manage/user'
import SuperTableCard from '~@/components/super-table-card/index.vue'
import UserEditModal from './components/user-edit-modal/index.vue'

// 用户类型枚举映射
const UserTypeMap: Record<any, { label: string, value: string }> = {
  'admin': { label: '管理员', value: 'admin' },
  'normal': { label: '普通用户', value: 'normal' },
}

const columns: TableColumnType<AccountInfoVO>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    customRender: ({ index }) => {
      return h('span', null, { default: () => index + 1 })
    },
    fixed: 'left',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
    width: 120,
    sorter: true,
  },
  {
    title: '登录名',
    dataIndex: 'loginName',
    width: 120,
    sorter: true,
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    width: 120,
    sorter: true,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNumber',
    width: 120,
    sorter: true,
  },
  {
    title: '类型',
    dataIndex: 'type',
    width: 100,
    customRender: ({ text }) => {
      const meta = UserTypeMap[text] ?? { label: String(text ?? ''), value: String(text ?? '') }
      return h('span', null, { default: () => meta.label })
    },
    sorter: true,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 160,
    fixed: 'right',
  },
]

const formSchema: FormItemSchema[] = [
  { name: 'userName', label: '用户名', type: 'input', placeholder: '输入用户名' },
  { name: 'loginName', label: '登录名', type: 'input', placeholder: '输入登录名' },
  { name: 'email', label: '邮箱', type: 'input', placeholder: '输入邮箱' },
  { name: 'phoneNumber', label: '手机号', type: 'input', placeholder: '输入手机号' },
  { name: 'type', label: '类型', type: 'select', options: Object.values(UserTypeMap) },
]

// 对话框与操作逻辑
const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)
// 修改弹窗组件引用（自动导入组件，无需显式import）
const editModalRef = ref<any>(null)
function openEdit(record: AccountInfoVO | Record<string, any>) {
  // 打开修改弹窗，传入当前行数据
  editModalRef.value?.open(record as AccountInfoVO)
}
async function handleDelete(record: AccountInfoVO | Record<string, any>) {
  const code = (record as AccountInfoVO).code ?? (record as any).code
  Modal.confirm({
    title: '确认删除该用户？',
    content: `确定删除用户【${(record as AccountInfoVO).userName}】？`,
    async onOk() {
      await deleteUser({ code })
      message.success('删除成功')
      superRef.value?.initQuery()
    },
  })
}
function handleModalOk() {
  message.success('操作成功')
  superRef.value?.initQuery()
}
</script>

<template>
  <page-container>
    <SuperTableCard
      ref="superRef"
      title="用户管理"
      :columns="columns"
      :form-schema="formSchema"
      :query-api="async (params) => await listUserPageable(params)"
      :table-props="{
        rowKey: 'code',
        scroll: { x: 1000 },
        bordered: true,
      }"
    >
      <!-- 表格工具栏扩展：新增按钮 -->
      <template #table-extra>
        <a-button type="primary" @click="editModalRef?.open()">新增</a-button>
      </template>
      <!-- 渲染操作列单元格 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="openEdit(record)">
              修改
            </a-button>
            <a-button type="link" danger @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </SuperTableCard>

    <!-- 用户修改弹窗：普通用户 -->
    <UserEditModal ref="editModalRef" @ok="handleModalOk" />
  </page-container>
</template>
