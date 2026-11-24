<script lang="ts" setup>
import type { FormInstance } from 'ant-design-vue'
import type { CodeNameDTO } from '~@/api/constant'
import type { DeptAddDTO, DeptTreeVO, DeptUpdateDTO } from '~@/api/org/dept'
import { message } from 'ant-design-vue'
import { addDept, queryDeptTree, updateDept } from '~@/api/org/dept'
import { listAllUser } from '~@/api/org/role'

const emit = defineEmits<(e: 'ok') => void>()

interface OpenOptions {
  record?: DeptTreeVO
  parentCode?: string
}

const visible = ref(false)
const confirmLoading = ref(false)
const isUpdate = ref(false)
const isRootDept = ref(false)
const formRef = ref<FormInstance>()

// 下拉选项：部门树与负责人列表
const deptTreeRaw = ref<DeptTreeVO[]>([])
const userOptions = ref<Array<{ label: string, value: string }>>([])

const formData = reactive<{
  code?: string
  parentCode: string
  name: string
  sortOrder: number
  leaderAccountCode?: string
  phone?: string
  email?: string
  status: number
}>({
  code: undefined,
  parentCode: '',
  name: '',
  sortOrder: 0,
  leaderAccountCode: undefined,
  phone: '',
  email: '',
  status: 1,
})

const title = computed(() => (isUpdate.value ? '修改部门' : '新增部门'))

// 收集当前节点及其所有子孙节点 code，编辑时禁止选作上级部门避免成环
function collectSubtreeCodes(list: DeptTreeVO[], targetCode: string): Set<string> {
  const result = new Set<string>()
  function collectAll(node: DeptTreeVO) {
    result.add(node.code)
    for (const child of node.children || [])
      collectAll(child)
  }
  function findTarget(nodes: DeptTreeVO[]) {
    for (const node of nodes || []) {
      if (node.code === targetCode) {
        collectAll(node)
        return true
      }
      if (node.children?.length && findTarget(node.children))
        return true
    }
    return false
  }
  findTarget(list)
  return result
}

// 转换为 TreeSelect 所需结构
const deptTreeSelectData = computed(() => {
  const disabledSet = isUpdate.value && formData.code
    ? collectSubtreeCodes(deptTreeRaw.value, formData.code)
    : new Set<string>()

  function mapNode(nodes: DeptTreeVO[]): any[] {
    return (nodes || []).map(node => ({
      title: node.name,
      value: node.code,
      key: node.code,
      disabled: disabledSet.has(node.code),
      children: node.children?.length ? mapNode(node.children) : undefined,
    }))
  }
  return mapNode(deptTreeRaw.value)
})

async function loadOptions() {
  const [treeRes, usersRes] = await Promise.all([
    queryDeptTree({}),
    listAllUser().catch(() => [] as CodeNameDTO[]),
  ])
  deptTreeRaw.value = treeRes || []
  userOptions.value = (usersRes || []).map(u => ({
    label: u.name,
    value: u.code,
  }))
}

async function open(options: OpenOptions = {}) {
  visible.value = true
  formRef.value?.clearValidate()

  await loadOptions()

  if (options.record) {
    const record = options.record
    isUpdate.value = true
    isRootDept.value = record.parentCode === '0'
    formData.code = record.code
    formData.parentCode = record.parentCode
    formData.name = record.name
    formData.sortOrder = record.sortOrder ?? 0
    formData.leaderAccountCode = record.leaderAccountCode || undefined
    formData.phone = record.phone || ''
    formData.email = record.email || ''
    formData.status = record.status ?? 1
  }
  else {
    isUpdate.value = false
    isRootDept.value = false
    formData.code = undefined
    formData.parentCode = options.parentCode || deptTreeRaw.value[0]?.code || ''
    formData.name = ''
    formData.sortOrder = 0
    formData.leaderAccountCode = undefined
    formData.phone = ''
    formData.email = ''
    formData.status = 1
  }
}

async function handleOk() {
  try {
    await formRef.value?.validate()
    confirmLoading.value = true
    if (isUpdate.value && formData.code) {
      const payload: DeptUpdateDTO = {
        code: formData.code,
        parentCode: formData.parentCode,
        name: formData.name.trim(),
        sortOrder: formData.sortOrder,
        leaderAccountCode: formData.leaderAccountCode || undefined,
        phone: formData.phone?.trim() || undefined,
        email: formData.email?.trim() || undefined,
        status: formData.status,
      }
      await updateDept(payload)
      message.success('部门修改成功')
    }
    else {
      const payload: DeptAddDTO = {
        parentCode: formData.parentCode,
        name: formData.name.trim(),
        sortOrder: formData.sortOrder,
        leaderAccountCode: formData.leaderAccountCode || undefined,
        phone: formData.phone?.trim() || undefined,
        email: formData.email?.trim() || undefined,
        status: formData.status,
      }
      await addDept(payload)
      message.success('部门新增成功')
    }
    visible.value = false
    emit('ok')
  }
  catch {
    // 校验或接口异常由全局提示，阻断关闭
  }
  finally {
    confirmLoading.value = false
  }
}

function handleCancel() {
  formRef.value?.resetFields()
  visible.value = false
}

defineExpose({ open })
</script>

<template>
  <a-modal
    v-model:open="visible"
    :title="title"
    :confirm-loading="confirmLoading"
    width="560px"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ style: { width: '96px' } }"
      :wrapper-col="{ span: 24 }"
    >
      <a-form-item
        v-if="!isRootDept"
        name="parentCode"
        label="上级部门"
        :rules="[{ required: true, message: '请选择上级部门' }]"
      >
        <a-tree-select
          v-model:value="formData.parentCode"
          :tree-data="deptTreeSelectData"
          tree-default-expand-all
          show-search
          tree-node-filter-prop="title"
          placeholder="请选择上级部门"
          style="width: 100%;"
        />
      </a-form-item>
      <a-form-item v-else label="上级部门">
        <a-input value="顶级部门（根）" disabled />
      </a-form-item>

      <a-form-item
        name="name"
        label="部门名称"
        :rules="[{ required: true, message: '请输入部门名称' }]"
      >
        <a-input v-model:value="formData.name" :maxlength="100" placeholder="请输入部门名称" />
      </a-form-item>

      <a-form-item name="sortOrder" label="显示排序">
        <a-input-number v-model:value="formData.sortOrder" :min="0" :max="99999" style="width: 100%;" />
      </a-form-item>

      <a-form-item name="leaderAccountCode" label="部门负责人">
        <a-select
          v-model:value="formData.leaderAccountCode"
          :options="userOptions"
          allow-clear
          show-search
          option-filter-prop="label"
          placeholder="请选择部门负责人"
        />
      </a-form-item>

      <a-form-item name="phone" label="联系电话">
        <a-input v-model:value="formData.phone" :maxlength="20" placeholder="请输入联系电话" />
      </a-form-item>

      <a-form-item name="email" label="联系邮箱">
        <a-input v-model:value="formData.email" :maxlength="100" placeholder="请输入联系邮箱" />
      </a-form-item>

      <a-form-item name="status" label="状态">
        <a-radio-group v-model:value="formData.status" :disabled="isRootDept">
          <a-radio :value="1">
            启用
          </a-radio>
          <a-radio :value="0">
            停用
          </a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
