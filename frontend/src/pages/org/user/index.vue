<script lang="ts" setup>
import type { TableColumnType } from 'ant-design-vue'
import type { DeptTreeVO } from '~@/api/org/dept'
import type { OrgUserInfoVO } from '~@/api/org/user'
import type { FormItemSchema } from '~@/components/query-form-card/schema'
import { message, Modal } from 'ant-design-vue'
import { queryDeptTree } from '~@/api/org/dept'
import { deleteUser, listUserPageable, updateUserStatus } from '~@/api/org/user'
import SuperTableCard from '~@/components/super-table-card/index.vue'
import UserEditModal from './components/user-edit-modal/index.vue'
import UserResetPwdModal from './components/user-reset-pwd-modal/index.vue'

const userStore = useUserStore()
const currentUserCode = computed(() => String(userStore.userInfo?.id ?? ''))
const isCurrentSuperAdmin = computed(() => userStore.isSuperAdmin)

// 用户状态选项（account.disabled：0 启用，1 禁用）
const disabledOptions = [
  { label: '启用', value: 0 },
  { label: '禁用', value: 1 },
]

// 左侧部门树状态
const deptLoading = ref(false)
const deptKeyword = ref('')
const deptTreeRaw = ref<DeptTreeVO[]>([])
const selectedDeptKeys = ref<string[]>([])
const expandedDeptKeys = ref<string[]>([])
const selectedDeptCode = ref<string | undefined>(undefined)
const selectedDeptName = ref<string>('')

// 将部门树递归按关键字过滤，保留匹配节点及其祖先
function filterDeptTree(nodes: DeptTreeVO[], keyword: string): DeptTreeVO[] {
  if (!keyword)
    return nodes
  const result: DeptTreeVO[] = []
  for (const node of nodes || []) {
    const children = node.children?.length ? filterDeptTree(node.children, keyword) : []
    if (node.name.includes(keyword) || children.length > 0) {
      result.push({
        ...node,
        children: children.length ? children : undefined,
      })
    }
  }
  return result
}

function collectDeptKeys(nodes: DeptTreeVO[]): string[] {
  const keys: string[] = []
  for (const node of nodes || []) {
    keys.push(node.code)
    if (node.children?.length)
      keys.push(...collectDeptKeys(node.children))
  }
  return keys
}

function toAntTreeData(nodes: DeptTreeVO[]): any[] {
  return (nodes || []).map(node => ({
    key: node.code,
    title: node.name,
    raw: node,
    children: node.children?.length ? toAntTreeData(node.children) : undefined,
  }))
}

const deptTreeData = computed(() => {
  const filtered = filterDeptTree(deptTreeRaw.value, deptKeyword.value.trim())
  return toAntTreeData(filtered)
})

// 监听搜索词变化时自动展开匹配树
watch(deptKeyword, () => {
  const filtered = filterDeptTree(deptTreeRaw.value, deptKeyword.value.trim())
  expandedDeptKeys.value = collectDeptKeys(filtered)
})

async function fetchDeptTree() {
  deptLoading.value = true
  try {
    const res = await queryDeptTree({})
    deptTreeRaw.value = res || []
    expandedDeptKeys.value = collectDeptKeys(deptTreeRaw.value)
  }
  finally {
    deptLoading.value = false
  }
}

// 表格列定义
const columns: TableColumnType<OrgUserInfoVO>[] = [
  {
    title: '序号',
    dataIndex: 'index',
    width: 70,
    align: 'center',
    customRender: ({ index }) => h('span', null, { default: () => index + 1 }),
    fixed: 'left',
  },
  {
    title: '姓名',
    dataIndex: 'userName',
    key: 'userName',
    width: 150,
    sorter: true,
    fixed: 'left',
  },
  {
    title: '登录名',
    dataIndex: 'loginName',
    key: 'loginName',
    width: 130,
    sorter: true,
  },
  {
    title: '主部门',
    dataIndex: 'primaryDeptName',
    key: 'primaryDeptName',
    width: 140,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '岗位',
    dataIndex: 'positionNames',
    key: 'positionNames',
    width: 160,
  },
  {
    title: '角色',
    dataIndex: 'roleNames',
    key: 'roleNames',
    width: 160,
  },
  {
    title: '手机号',
    dataIndex: 'phoneNumber',
    key: 'phoneNumber',
    width: 130,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
    width: 170,
    ellipsis: true,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '状态',
    dataIndex: 'disabled',
    key: 'disabled',
    width: 90,
    align: 'center',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 170,
    sorter: true,
  },
  {
    title: '操作',
    key: 'action',
    width: 240,
    fixed: 'right',
  },
]

// 查询表单 Schema
const formSchema: FormItemSchema[] = [
  { name: 'userName', label: '姓名', type: 'input', placeholder: '请输入姓名' },
  { name: 'loginName', label: '登录名', type: 'input', placeholder: '请输入登录名' },
  { name: 'phoneNumber', label: '手机号', type: 'input', placeholder: '请输入手机号' },
  { name: 'disabled', label: '状态', type: 'select', placeholder: '请选择状态', options: disabledOptions },
  { name: 'email', label: '邮箱', type: 'input', placeholder: '请输入邮箱', advanced: true },
]

const superRef = ref<InstanceType<typeof SuperTableCard> | null>(null)
const editModalRef = ref<InstanceType<typeof UserEditModal> | null>(null)
const resetPwdModalRef = ref<InstanceType<typeof UserResetPwdModal> | null>(null)

// 包装查询接口，自动注入左侧选中的 deptCode
async function queryUserList(params: Record<string, any>) {
  return listUserPageable({
    ...params,
    deptCode: selectedDeptCode.value,
  })
}

// 点击部门树节点
function handleDeptSelect(keys: (string | number)[], info: any) {
  if (keys.length > 0) {
    selectedDeptKeys.value = [String(keys[0])]
    selectedDeptCode.value = String(keys[0])
    selectedDeptName.value = info?.node?.title || ''
  }
  else {
    selectedDeptKeys.value = []
    selectedDeptCode.value = undefined
    selectedDeptName.value = ''
  }
  superRef.value?.initQuery()
}

// 重置为全部部门
function handleSelectAllDept() {
  selectedDeptKeys.value = []
  selectedDeptCode.value = undefined
  selectedDeptName.value = ''
  superRef.value?.initQuery()
}

// 是否受保护（超管账号不可删除/禁用；非超管不可编辑/重置超管密码；不可删除/禁用自己）
function isSuperAdminRow(record: Record<string, any>) {
  return record.superAdmin === 1
}
function isSelfRow(record: Record<string, any>) {
  return !!currentUserCode.value && record.code === currentUserCode.value
}
function canEditRow(record: Record<string, any>) {
  return !isSuperAdminRow(record) || isCurrentSuperAdmin.value
}
function canToggleStatusRow(record: Record<string, any>) {
  return !isSuperAdminRow(record) && !isSelfRow(record)
}
function canDeleteRow(record: Record<string, any>) {
  return !isSuperAdminRow(record) && !isSelfRow(record)
}

function openAdd() {
  editModalRef.value?.open({ defaultDeptCode: selectedDeptCode.value })
}

function openEdit(record: OrgUserInfoVO | Record<string, any>) {
  editModalRef.value?.open({ record: record as OrgUserInfoVO })
}

function openResetPwd(record: OrgUserInfoVO | Record<string, any>) {
  resetPwdModalRef.value?.open(record as OrgUserInfoVO)
}

function handleToggleStatus(record: OrgUserInfoVO | Record<string, any>) {
  const user = record as OrgUserInfoVO
  const targetDisabled = user.disabled === 1 ? 0 : 1
  const actionText = targetDisabled === 0 ? '启用' : '禁用'
  Modal.confirm({
    title: `确认${actionText}该用户？`,
    content: `确定${actionText}用户【${user.userName}】？${targetDisabled === 1 ? '禁用后该用户将被强制下线。' : ''}`,
    async onOk() {
      await updateUserStatus({ code: user.code, disabled: targetDisabled })
      message.success(`${actionText}成功`)
      superRef.value?.initQuery()
    },
  })
}

function handleDelete(record: OrgUserInfoVO | Record<string, any>) {
  const user = record as OrgUserInfoVO
  Modal.confirm({
    title: '确认删除该用户？',
    content: `确定删除用户【${user.userName}】？`,
    async onOk() {
      await deleteUser({ code: user.code })
      message.success('删除成功')
      superRef.value?.initQuery()
    },
  })
}

onMounted(() => {
  fetchDeptTree()
})
</script>

<template>
  <page-container>
    <a-row :gutter="16">
      <!-- 左侧部门树 -->
      <a-col :xs="24" :md="6" :xl="5" style="margin-bottom: 16px;">
        <a-card title="组织部门" :bordered="false" size="small" style="height: 100%;">
          <template #extra>
            <a-button type="link" size="small" @click="handleSelectAllDept">
              全部部门
            </a-button>
          </template>

          <a-input-search
            v-model:value="deptKeyword"
            placeholder="搜索部门名称"
            allow-clear
            style="margin-bottom: 12px;"
          />

          <a-spin :spinning="deptLoading">
            <a-tree
              v-if="deptTreeData.length"
              v-model:selected-keys="selectedDeptKeys"
              v-model:expanded-keys="expandedDeptKeys"
              :tree-data="deptTreeData"
              block-node
              @select="handleDeptSelect"
            />
            <a-empty v-else description="暂无部门数据" />
          </a-spin>
        </a-card>
      </a-col>

      <!-- 右侧用户列表 -->
      <a-col :xs="24" :md="18" :xl="19">
        <SuperTableCard
          ref="superRef"
          :title="selectedDeptName ? `用户管理 - ${selectedDeptName}` : '用户管理'"
          :columns="columns"
          :form-schema="formSchema"
          :query-api="queryUserList"
          :table-props="{
            rowKey: 'code',
            scroll: { x: 1300 },
            bordered: true,
          }"
        >
          <template #table-extra>
            <a-space>
              <a-tag v-if="selectedDeptName" closable color="blue" @close="handleSelectAllDept">
                部门：{{ selectedDeptName }}
              </a-tag>
              <a-button type="primary" @click="openAdd">
                新增用户
              </a-button>
            </a-space>
          </template>

          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'userName'">
              <a-space :size="4">
                <span>{{ record.userName }}</span>
                <a-tag v-if="record.superAdmin === 1" color="gold">
                  超管
                </a-tag>
              </a-space>
            </template>

            <template v-else-if="column.key === 'positionNames'">
              <template v-if="record.positionNames?.length">
                <a-tag v-for="pos in record.positionNames" :key="pos" color="geekblue" style="margin-bottom: 2px;">
                  {{ pos }}
                </a-tag>
              </template>
              <span v-else>-</span>
            </template>

            <template v-else-if="column.key === 'roleNames'">
              <template v-if="record.roleNames?.length">
                <a-tag v-for="role in record.roleNames" :key="role" color="blue" style="margin-bottom: 2px;">
                  {{ role }}
                </a-tag>
              </template>
              <span v-else>-</span>
            </template>

            <template v-else-if="column.key === 'disabled'">
              <a-tag :color="record.disabled === 0 ? 'success' : 'error'">
                {{ record.disabled === 0 ? '启用' : '禁用' }}
              </a-tag>
            </template>

            <template v-else-if="column.key === 'action'">
              <a-space :size="4">
                <a-button
                  type="link"
                  size="small"
                  :disabled="!canEditRow(record)"
                  @click="openEdit(record)"
                >
                  修改
                </a-button>
                <a-button
                  type="link"
                  size="small"
                  :disabled="!canToggleStatusRow(record)"
                  @click="handleToggleStatus(record)"
                >
                  {{ record.disabled === 0 ? '禁用' : '启用' }}
                </a-button>
                <a-button
                  type="link"
                  size="small"
                  :disabled="!canEditRow(record)"
                  @click="openResetPwd(record)"
                >
                  重置密码
                </a-button>
                <a-button
                  type="link"
                  size="small"
                  danger
                  :disabled="!canDeleteRow(record)"
                  @click="handleDelete(record)"
                >
                  删除
                </a-button>
              </a-space>
            </template>
          </template>
        </SuperTableCard>
      </a-col>
    </a-row>

    <!-- 用户新增/编辑弹窗 -->
    <UserEditModal ref="editModalRef" @ok="superRef?.initQuery()" />
    <!-- 重置密码弹窗 -->
    <UserResetPwdModal ref="resetPwdModalRef" />
  </page-container>
</template>
