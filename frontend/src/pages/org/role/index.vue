<script setup lang="ts">
import type { TableColumnType } from 'ant-design-vue'
import type { RoleInfoVO } from '~@/api/org/role'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { message, Modal } from 'ant-design-vue'
import { DATA_SCOPE_MAP, deleteRole, listRolePageable } from '~@/api/org/role'
import SuperTableCard from '~@/components/super-table-card/index.vue'
import RoleDataScopeModal from './components/role-data-scope-modal/index.vue'
import RoleEditModal from './components/role-edit-modal/index.vue'
import RolePermissionModal from './components/role-permission-modal/index.vue'
import RoleUserModal from './components/role-user-modal/index.vue'

// 列定义
const columns: TableColumnType<RoleInfoVO>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 80,
    align: 'center',
    customRender: ({ index }) => h('span', null, { default: () => index + 1 }),
    fixed: 'left',
  },
  {
    title: '角色名称',
    dataIndex: 'name',
    key: 'name',
    width: 200,
    sorter: true,
  },
  {
    title: '数据权限范围',
    dataIndex: 'dataScope',
    key: 'dataScope',
    width: 200,
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
    width: 300,
    fixed: 'right',
  },
]

// 查询表单定义
const formSchema: FormItemSchema[] = [
  { label: '角色名称', name: 'name', type: 'input', placeholder: '请输入角色名称' },
]

const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)
const editModalRef = ref<InstanceType<typeof RoleEditModal> | null>(null)
const permissionModalRef = ref<InstanceType<typeof RolePermissionModal> | null>(null)
const dataScopeModalRef = ref<InstanceType<typeof RoleDataScopeModal> | null>(null)
const userModalRef = ref<InstanceType<typeof RoleUserModal> | null>(null)

function getDataScopeMeta(record: Record<string, any>) {
  const scope = Number(record.dataScope)
  const meta = DATA_SCOPE_MAP[scope]
  return {
    label: record.dataScopeName || meta?.label || '仅本人数据权限',
    color: meta?.color || 'default',
  }
}

function openEdit(record?: RoleInfoVO | Record<string, any>) {
  editModalRef.value?.open(record as RoleInfoVO | undefined)
}

function handleDelete(record: RoleInfoVO | Record<string, any>) {
  const role = record as RoleInfoVO
  Modal.confirm({
    title: '确认删除',
    content: `确定删除角色【${role.name}】？`,
    async onOk() {
      await deleteRole({ code: role.code })
      message.success('删除成功')
      superRef.value?.initQuery()
    },
  })
}

function openPermission(record: RoleInfoVO | Record<string, any>) {
  permissionModalRef.value?.open((record as RoleInfoVO).code)
}

function openDataScope(record: RoleInfoVO | Record<string, any>) {
  dataScopeModalRef.value?.open(record as RoleInfoVO)
}

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
      <template #table-extra>
        <a-button type="primary" @click="openEdit()">
          新增角色
        </a-button>
      </template>

      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'dataScope'">
          <a-tag :color="getDataScopeMeta(record).color">
            {{ getDataScopeMeta(record).label }}
          </a-tag>
        </template>

        <template v-else-if="column.key === 'action'">
          <a-space :size="4">
            <a-button type="link" size="small" @click="openPermission(record)">
              菜单权限
            </a-button>
            <a-button type="link" size="small" @click="openDataScope(record)">
              数据权限
            </a-button>
            <a-button type="link" size="small" @click="openUsers(record)">
              角色用户
            </a-button>
            <a-button type="link" size="small" @click="openEdit(record)">
              修改
            </a-button>
            <a-button type="link" size="small" danger @click="handleDelete(record)">
              删除
            </a-button>
          </a-space>
        </template>
      </template>
    </SuperTableCard>

    <!-- 角色新增/编辑弹窗 -->
    <RoleEditModal ref="editModalRef" @ok="superRef?.initQuery()" />
    <!-- 菜单功能权限配置弹窗 -->
    <RolePermissionModal ref="permissionModalRef" @ok="superRef?.initQuery()" />
    <!-- 数据权限配置弹窗 -->
    <RoleDataScopeModal ref="dataScopeModalRef" @ok="superRef?.initQuery()" />
    <!-- 角色成员管理弹窗 -->
    <RoleUserModal ref="userModalRef" @ok="superRef?.initQuery()" />
  </page-container>
</template>
