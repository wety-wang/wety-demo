<script setup lang="ts">
import type { CodeNameDTO } from '~@/api/constant'
import type { RoleUserInfoVO } from '~@/api/user-manage/role'
import { message, Modal } from 'ant-design-vue'
import { addRoleUser, deleteRoleUser, listAllUser, listRoleUserPageable } from '~@/api/user-manage/role'

// 主弹窗状态
const visible = ref(false)
const roleCode = ref<string>('')
const loading = ref(false)

// 查询条件与分页（角色关联的用户）
const query = reactive({ userName: '' })
const pageNumber = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dataSource = ref<RoleUserInfoVO[]>([])

// 打开弹窗并加载数据
async function open(code: string) {
  roleCode.value = code
  visible.value = true
  pageNumber.value = 1
  await fetchList()
}

// 拉取角色下用户列表
async function fetchList() {
  loading.value = true
  try {
    const page = await listRoleUserPageable({ roleCode: roleCode.value, userName: query.userName || undefined, pageNumber: pageNumber.value, pageSize: pageSize.value })
    dataSource.value = page?.content || []
    total.value = page?.totalElements || 0
  }
  finally {
    loading.value = false
  }
}

// 删除用户关联
async function handleDelete(record: RoleUserInfoVO) {
  Modal.confirm({
    title: '确认删除',
    content: `确定将用户【${record.accountName || record.accountCode}】从角色移除？`,
    async onOk() {
      await deleteRoleUser({ roleCode: record.roleCode, accountCode: record.accountCode })
      message.success('删除成功')
      await fetchList()
    },
  })
}

// 关闭弹窗
function close() {
  visible.value = false
}

// 打开“添加用户”选择弹窗
const addVisible = ref(false)
const addLoading = ref(false)
// 选择用户列表与分页
// 根据接口返回结构 CodeNameDTO，仅包含 code、name，这里使用 name 作为筛选字段
const addQuery = reactive({ name: '' })
const addPageNumber = ref(1)
const addPageSize = ref(10)
// 原始数据列表（后端一次性返回全部用户，不分页）
const addDataSourceRaw = ref<CodeNameDTO[]>([])
// 选中行主键：与 antd Table 的 Key 类型保持一致（string | number）
const selectedRowKeys = ref<(string | number)[]>([])

// 计算筛选后的总数（用于前端分页 total）
const addTotalComputed = computed(() => {
  const q = addQuery.name?.trim()
  const list = addDataSourceRaw.value || []
  // 前端模糊筛选：name 包含关键字
  const filtered = q ? list.filter(item => (item.name || '').includes(q)) : list
  return filtered.length
})

// 表格展示数据：在前端进行筛选 + 分页切片
const addTableData = computed(() => {
  const q = addQuery.name?.trim()
  const list = addDataSourceRaw.value || []
  const filtered = q ? list.filter(item => (item.name || '').includes(q)) : list
  const start = (addPageNumber.value - 1) * addPageSize.value
  const end = start + addPageSize.value
  return filtered.slice(start, end)
})

async function openAdd() {
  addVisible.value = true
  selectedRowKeys.value = []
  addPageNumber.value = 1
  await fetchAddList()
}

async function fetchAddList() {
  addLoading.value = true
  try {
    const res = await listAllUser()
    // 缓存全部用户用于前端筛选与分页
    addDataSourceRaw.value = res || []
  }
  finally {
    addLoading.value = false
  }
}

async function handleAddOk() {
  if (!selectedRowKeys.value.length) {
    message.warning('请先选择需要添加的用户')
    return
  }
  // 点击确定时展示加载态，直到接口返回
  addLoading.value = true
  try {
    // 接口入参需要 string[]，row-key 为 code（字符串），此处统一转换为字符串数组
    const accountCodes = selectedRowKeys.value.map(k => String(k))
    await addRoleUser({ roleCode: roleCode.value, accountCodes })
    message.success('添加成功')
    addVisible.value = false
    await fetchList()
  }
  finally {
    addLoading.value = false
  }
}

// 行选择事件：与 antd Table 的 onChange 签名一致（selectedRowKeys: Key[], selectedRows: any[]）
function onAddSelectionChange(keys: (string | number)[], _rows: any[]) {
  selectedRowKeys.value = keys
}

defineExpose({ open })
</script>

<template>
  <a-modal v-model:open="visible" title="角色用户" width="900px" :confirm-loading="loading" @cancel="close">
    <!-- 查询工具栏 -->
    <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center;">
      <a-input v-model:value="query.userName" allow-clear placeholder="按用户名称筛选" style="width: 240px;" />
      <a-button type="primary" @click="fetchList">
        查询
      </a-button>
      <a-button @click="() => { query.userName = ''; fetchList() }">
        重置
      </a-button>
      <a-button type="primary" ghost style="margin-left: auto" @click="openAdd">
        添加用户
      </a-button>
    </div>

    <!-- 角色下的用户列表 -->
    <a-table
      :data-source="dataSource"
      :loading="loading"
      :pagination="{ current: pageNumber, pageSize, total, onChange: (p, s) => { pageNumber = p; pageSize = s; fetchList() } }"
      row-key="accountCode"
      bordered
      size="middle"
    >
      <a-table-column key="accountName" title="用户名称" data-index="accountName" />
      <a-table-column key="action" title="操作" :width="120">
        <template #default="{ record }">
          <a-button type="link" danger @click="handleDelete(record)">
            删除
          </a-button>
        </template>
      </a-table-column>
    </a-table>

    <!-- 添加用户选择弹窗 -->
    <a-modal v-model:open="addVisible" title="选择用户" width="900px" :confirm-loading="addLoading" @ok="handleAddOk" @cancel="() => addVisible = false">
      <div style="margin-bottom: 12px; display: flex; gap: 8px; align-items: center;">
        <a-input v-model:value="addQuery.name" allow-clear placeholder="按用户名称筛选" style="width: 240px;" />
        <a-button type="primary" @click="() => { addPageNumber = 1 }">
          查询
        </a-button>
        <a-button @click="() => { addQuery.name = ''; addPageNumber = 1 }">
          重置
        </a-button>
      </div>
      <a-table
        :data-source="addTableData"
        :loading="addLoading"
        :pagination="{ current: addPageNumber, pageSize: addPageSize, total: addTotalComputed, onChange: (p, s) => { addPageNumber = p; addPageSize = s } }"
        row-key="code"
        bordered
        size="middle"
        :row-selection="{ selectedRowKeys, onChange: onAddSelectionChange }"
      >
        <a-table-column key="name" title="用户名称" data-index="name" />
      </a-table>
    </a-modal>
  </a-modal>
</template>
