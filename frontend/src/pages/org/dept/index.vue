<script lang="ts" setup>
import type { TableColumnType } from 'ant-design-vue'
import type { DeptTreeVO } from '~@/api/org/dept'
import { message, Modal } from 'ant-design-vue'
import { deleteDept, queryDeptTree, updateDeptStatus } from '~@/api/org/dept'
import DeptEditModal from './components/dept-edit-modal/index.vue'

// 状态选项
const statusOptions = [
  { label: '启用', value: 1 },
  { label: '停用', value: 0 },
]

// 查询条件
const queryParams = reactive<{ name?: string, status?: number }>({
  name: undefined,
  status: undefined,
})

// 表格数据与加载态
const loading = ref(false)
const treeData = ref<DeptTreeVO[]>([])
const expandedRowKeys = ref<string[]>([])

// 编辑弹窗引用
const editModalRef = ref<InstanceType<typeof DeptEditModal> | null>(null)

// 列定义
const columns: TableColumnType<DeptTreeVO>[] = [
  {
    title: '部门名称',
    dataIndex: 'name',
    key: 'name',
    width: 220,
    fixed: 'left',
  },
  {
    title: '部门编码',
    dataIndex: 'code',
    key: 'code',
    width: 180,
  },
  {
    title: '负责人',
    dataIndex: 'leaderAccountName',
    key: 'leaderAccountName',
    width: 120,
    customRender: ({ text }) => text || '-',
  },
  {
    title: '排序',
    dataIndex: 'sortOrder',
    key: 'sortOrder',
    width: 80,
    align: 'center',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    width: 90,
    align: 'center',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
  },
  {
    title: '操作',
    key: 'action',
    width: 240,
    fixed: 'right',
  },
]

// 收集所有节点 code，用于全部展开
function collectAllKeys(list: DeptTreeVO[]): string[] {
  const keys: string[] = []
  for (const item of list || []) {
    keys.push(item.code)
    if (item.children?.length)
      keys.push(...collectAllKeys(item.children))
  }
  return keys
}

// 查询部门树
async function fetchDeptTree() {
  loading.value = true
  try {
    const res = await queryDeptTree({
      name: queryParams.name?.trim() || undefined,
      status: queryParams.status,
    })
    treeData.value = res || []
    // 默认展开所有节点，方便查看层级结构
    expandedRowKeys.value = collectAllKeys(treeData.value)
  }
  finally {
    loading.value = false
  }
}

function handleSearch() {
  fetchDeptTree()
}

function handleReset() {
  queryParams.name = undefined
  queryParams.status = undefined
  fetchDeptTree()
}

function expandAll() {
  expandedRowKeys.value = collectAllKeys(treeData.value)
}

function collapseAll() {
  expandedRowKeys.value = []
}

// 新增根部门（或默认以第一个根节点为上级）
function openAddRoot() {
  const defaultParent = treeData.value[0]?.code
  editModalRef.value?.open({ parentCode: defaultParent })
}

// 行内新增子部门
function openAddChild(record: DeptTreeVO | Record<string, any>) {
  editModalRef.value?.open({ parentCode: (record as DeptTreeVO).code })
}

// 编辑部门
function openEdit(record: DeptTreeVO | Record<string, any>) {
  editModalRef.value?.open({ record: record as DeptTreeVO })
}

// 启用/停用切换
function handleToggleStatus(record: DeptTreeVO | Record<string, any>) {
  const dept = record as DeptTreeVO
  const targetStatus = dept.status === 1 ? 0 : 1
  const actionText = targetStatus === 1 ? '启用' : '停用'
  Modal.confirm({
    title: `确认${actionText}该部门？`,
    content: `确定${actionText}部门【${dept.name}】？`,
    async onOk() {
      await updateDeptStatus({ code: dept.code, status: targetStatus })
      message.success(`${actionText}成功`)
      await fetchDeptTree()
    },
  })
}

// 删除部门
function handleDelete(record: DeptTreeVO | Record<string, any>) {
  const dept = record as DeptTreeVO
  Modal.confirm({
    title: '确认删除该部门？',
    content: `确定删除部门【${dept.name}】？删除后不可恢复。`,
    async onOk() {
      await deleteDept({ code: dept.code })
      message.success('删除成功')
      await fetchDeptTree()
    },
  })
}

onMounted(() => {
  fetchDeptTree()
})
</script>

<template>
  <page-container>
    <!-- 查询条件卡片 -->
    <a-card :bordered="false" style="margin-bottom: 16px;">
      <a-form layout="inline" @submit.prevent="handleSearch">
        <a-form-item label="部门名称">
          <a-input
            v-model:value="queryParams.name"
            placeholder="请输入部门名称"
            allow-clear
            style="width: 220px;"
            @press-enter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="queryParams.status"
            placeholder="请选择状态"
            allow-clear
            :options="statusOptions"
            style="width: 140px;"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" :loading="loading" @click="handleSearch">
              查询
            </a-button>
            <a-button @click="handleReset">
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 树形表格卡片 -->
    <a-card title="部门管理" :bordered="false">
      <template #extra>
        <a-space>
          <a-button @click="expandAll">
            展开全部
          </a-button>
          <a-button @click="collapseAll">
            折叠全部
          </a-button>
          <a-button @click="fetchDeptTree">
            刷新
          </a-button>
          <a-button type="primary" @click="openAddRoot">
            新增部门
          </a-button>
        </a-space>
      </template>

      <a-table
        v-model:expanded-row-keys="expandedRowKeys"
        :columns="columns"
        :data-source="treeData"
        :loading="loading"
        :pagination="false"
        row-key="code"
        bordered
        :scroll="{ x: 1100 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 1 ? 'success' : 'default'">
              {{ record.status === 1 ? '启用' : '停用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="4">
              <a-button type="link" size="small" @click="openAddChild(record)">
                新增子部门
              </a-button>
              <a-button type="link" size="small" @click="openEdit(record)">
                修改
              </a-button>
              <a-button
                type="link"
                size="small"
                :disabled="record.parentCode === '0' && record.status === 1"
                @click="handleToggleStatus(record)"
              >
                {{ record.status === 1 ? '停用' : '启用' }}
              </a-button>
              <a-button
                type="link"
                size="small"
                danger
                :disabled="record.parentCode === '0'"
                @click="handleDelete(record)"
              >
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 部门新增/编辑弹窗 -->
    <DeptEditModal ref="editModalRef" @ok="fetchDeptTree" />
  </page-container>
</template>
