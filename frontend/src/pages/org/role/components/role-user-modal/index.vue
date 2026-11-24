<script setup lang="ts">
import type { CodeNameDTO } from '~@/api/constant'
import type { RoleUserInfoVO } from '~@/api/org/role'
import { message, Modal } from 'ant-design-vue'
import { addRoleUser, deleteRoleUser, listAllUser, listRoleUserPageable } from '~@/api/org/role'

const visible = ref(false)
const roleCode = ref<string>('')
const loading = ref(false)

const query = reactive({ userName: '' })
const pageNumber = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dataSource = ref<RoleUserInfoVO[]>([])

async function open(code: string) {
  roleCode.value = code
  visible.value = true
  pageNumber.value = 1
  await fetchList()
}

async function fetchList() {
  loading.value = true
  try {
    const page = await listRoleUserPageable({
      roleCode: roleCode.value,
      userName: query.userName || undefined,
      pageNumber: pageNumber.value,
      pageSize: pageSize.value,
    })
    dataSource.value = page?.content || []
    total.value = page?.totalElements || 0
  }
  finally {
    loading.value = false
  }
}

async function handleDelete(record: RoleUserInfoVO) {
  Modal.confirm({
    title: '确认移除',
    content: `确定将用户【${record.accountName || record.accountCode}】从角色移除？`,
    async onOk() {
      await deleteRoleUser({ roleCode: record.roleCode, accountCode: record.accountCode })
      message.success('移除成功')
      await fetchList()
    },
  })
}

function close() {
  visible.value = false
}

// 添加用户选择弹窗
const addVisible = ref(false)
const addLoading = ref(false)
const addQuery = reactive({ name: '' })
const addPageNumber = ref(1)
const addPageSize = ref(10)
const addDataSourceRaw = ref<CodeNameDTO[]>([])
const selectedRowKeys = ref<(string | number)[]>([])

const addTotalComputed = computed(() => {
  const q = addQuery.name?.trim()
  const list = addDataSourceRaw.value || []
  const filtered = q ? list.filter(item => (item.name || '').includes(q)) : list
  return filtered.length
})

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
  addLoading.value = true
  try {
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

function onAddSelectionChange(keys: (string | number)[], _rows: any[]) {
  selectedRowKeys.value = keys
}

defineExpose({ open })
</script>

<template>
  <a-modal v-model:open="visible" title="角色成员" width="900px" :confirm-loading="loading" :footer="null" @cancel="close">
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
            移除
          </a-button>
        </template>
      </a-table-column>
    </a-table>

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
