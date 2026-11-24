<script setup lang="ts">
import type { TableColumnType } from 'ant-design-vue'
import type { RoleInfoVO } from '~@/api/user-manage/role'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { message, Modal } from 'ant-design-vue'
import { deleteRole, listRolePageable } from '~@/api/user-manage/role'
import SuperTableCard from '~@/components/super-table-card/index.vue'
import RoleEditModal from './components/role-edit-modal/index.vue'
import RolePermissionModal from './components/role-permission-modal/index.vue'
import RoleUserModal from './components/role-user-modal/index.vue'

// 列定义
const columns: TableColumnType<RoleInfoVO>[] = [
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
    title: '角色名称',
    dataIndex: 'name',
    key: 'name',
    sorter: true,

  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    fixed: 'right',
  },
]

// 查询表单定义
const formSchema: FormItemSchema[] = [
  { label: '角色名称', name: 'name', type: 'input' },
]

// table引用
const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)

// 弹窗引用
const editModalRef = ref<any>(null)
const permissionModalRef = ref<any>(null)
const userModalRef = ref<any>(null)

// 打开修改弹窗（模板插槽 record 推断为 Record<string, any>，放宽签名以兼容）
function openEdit(record?: RoleInfoVO | Record<string, any>) {
  editModalRef.value?.open(record as RoleInfoVO)
}

// 删除角色（模板传参类型为 Record，内部强制断言为 RoleInfoVO）
function handleDelete(record: RoleInfoVO | Record<string, any>) {
  Modal.confirm({
    title: '确认删除',
    content: `确定删除角色【${(record as RoleInfoVO).name}】？`,
    async onOk() {
      await deleteRole({ code: (record as RoleInfoVO).code })
      message.success('删除成功')
      superRef.value?.initQuery()
    },
  })
}

// 打开权限弹窗
function openPermission(record: RoleInfoVO | Record<string, any>) {
  permissionModalRef.value?.open((record as RoleInfoVO).code)
}

// 打开角色用户弹窗
function openUsers(record: RoleInfoVO | Record<string, any>) {
  userModalRef.value?.open((record as RoleInfoVO).code)
}
</script>

<template>
  <page-container>
    <SuperTableCard
      ref="superRef"
      title="角色管理"
      :columns="columns"
      :form-schema="formSchema"
      :query-api="async (params) => await listRolePageable(params)"
      :table-props="{
        rowKey: 'code',
        scroll: { x: 1000 },
        bordered: true,
      }"
    >
      <!-- 表格工具栏扩展：新增按钮 -->
      <template #table-extra>
        <a-button type="primary" @click="editModalRef?.open()">
          新增
        </a-button>
      </template>
      <!-- 渲染操作列 -->
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="openPermission(record)">
              角色权限
            </a-button>
            <a-button type="link" @click="openUsers(record)">
              角色用户
            </a-button>
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

    <!-- 角色修改弹窗 -->
    <RoleEditModal ref="editModalRef" @ok="superRef?.initQuery()" />
    <!-- 权限配置弹窗 -->
    <RolePermissionModal ref="permissionModalRef" @ok="superRef?.initQuery()" />
    <!-- 角色用户弹窗 -->
    <RoleUserModal ref="userModalRef" @ok="superRef?.initQuery()" />
  </page-container>
</template>
