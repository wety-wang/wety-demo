<script setup lang="ts">
import type { DeptTreeVO } from '~@/api/org/dept'
import type { RoleInfoVO } from '~@/api/org/role'
import { message } from 'ant-design-vue'
import { queryDeptTree } from '~@/api/org/dept'
import { DATA_SCOPE_OPTIONS, queryRoleDataScope, updateRoleDataScope } from '~@/api/org/role'

const emit = defineEmits<(e: 'ok') => void>()

const visible = ref(false)
const loading = ref(false)
const confirmLoading = ref(false)

const roleCode = ref('')
const roleName = ref('')
const dataScope = ref<number>(5)

// 部门树与勾选控制
const deptTreeRaw = ref<DeptTreeVO[]>([])
const expandedKeys = ref<string[]>([])
const checkedDeptCodes = ref<string[]>([])
const expandAllFlag = ref(true)
const checkAllFlag = ref(false)
const linkParentChild = ref(false)

function collectAllKeys(nodes: DeptTreeVO[]): string[] {
  const keys: string[] = []
  for (const node of nodes || []) {
    keys.push(node.code)
    if (node.children?.length)
      keys.push(...collectAllKeys(node.children))
  }
  return keys
}

function toAntTreeData(nodes: DeptTreeVO[]): any[] {
  return (nodes || []).map(node => ({
    key: node.code,
    title: node.name,
    children: node.children?.length ? toAntTreeData(node.children) : undefined,
  }))
}

const deptTreeData = computed(() => toAntTreeData(deptTreeRaw.value))
const allDeptKeys = computed(() => collectAllKeys(deptTreeRaw.value))

// 兼容 checkStrictly 模式与非严格模式的 checkedKeys 绑定格式
const treeCheckedKeys = computed(() => {
  if (!linkParentChild.value) {
    return {
      checked: checkedDeptCodes.value,
      halfChecked: [],
    }
  }
  return checkedDeptCodes.value
})

function handleTreeCheck(keys: any) {
  if (Array.isArray(keys)) {
    checkedDeptCodes.value = keys.map(String)
  }
  else if (keys && Array.isArray(keys.checked)) {
    checkedDeptCodes.value = keys.checked.map(String)
  }
  checkAllFlag.value = allDeptKeys.value.length > 0 && checkedDeptCodes.value.length === allDeptKeys.value.length
}

function handleExpandAllChange(e: any) {
  const checked = !!e?.target?.checked
  expandAllFlag.value = checked
  expandedKeys.value = checked ? [...allDeptKeys.value] : []
}

function handleCheckAllChange(e: any) {
  const checked = !!e?.target?.checked
  checkAllFlag.value = checked
  checkedDeptCodes.value = checked ? [...allDeptKeys.value] : []
}

async function open(record: RoleInfoVO) {
  roleCode.value = record.code
  roleName.value = record.name
  visible.value = true
  loading.value = true
  try {
    const [scopeRes, deptRes] = await Promise.all([
      queryRoleDataScope({ code: record.code }),
      queryDeptTree({}),
    ])
    deptTreeRaw.value = deptRes || []
    dataScope.value = scopeRes?.dataScope ?? record.dataScope ?? 5
    checkedDeptCodes.value = [...(scopeRes?.deptCodes || [])]
    expandedKeys.value = [...allDeptKeys.value]
    expandAllFlag.value = true
    checkAllFlag.value = allDeptKeys.value.length > 0 && checkedDeptCodes.value.length === allDeptKeys.value.length
  }
  finally {
    loading.value = false
  }
}

function close() {
  visible.value = false
}

async function handleOk() {
  if (dataScope.value === 2 && checkedDeptCodes.value.length === 0) {
    message.warning('自定义部门数据权限至少需要选择一个部门')
    return
  }
  confirmLoading.value = true
  try {
    await updateRoleDataScope({
      roleCode: roleCode.value,
      dataScope: dataScope.value,
      deptCodes: dataScope.value === 2 ? checkedDeptCodes.value : [],
    })
    message.success('数据权限修改成功')
    close()
    emit('ok')
  }
  catch {
    // 接口异常由全局拦截器提示
  }
  finally {
    confirmLoading.value = false
  }
}

defineExpose({ open })
</script>

<template>
  <a-modal
    v-model:open="visible"
    title="分配数据权限"
    :confirm-loading="confirmLoading"
    width="560px"
    @ok="handleOk"
    @cancel="close"
  >
    <a-spin :spinning="loading">
      <a-form :label-col="{ style: { width: '96px' } }" :wrapper-col="{ span: 24 }">
        <a-form-item label="角色名称">
          <a-input :value="roleName" disabled />
        </a-form-item>

        <a-form-item label="权限范围">
          <a-select
            v-model:value="dataScope"
            :options="DATA_SCOPE_OPTIONS.map(item => ({ label: item.label, value: item.value }))"
            placeholder="请选择数据权限范围"
          />
        </a-form-item>

        <a-form-item v-if="dataScope === 2" label="数据部门" required>
          <div style="margin-bottom: 8px;">
            <a-space>
              <a-checkbox :checked="expandAllFlag" @change="handleExpandAllChange">
                展开/折叠
              </a-checkbox>
              <a-checkbox :checked="checkAllFlag" @change="handleCheckAllChange">
                全选/全不选
              </a-checkbox>
              <a-checkbox v-model:checked="linkParentChild">
                父子联动
              </a-checkbox>
            </a-space>
          </div>
          <div style="border: 1px solid #d9d9d9; border-radius: 6px; padding: 8px 12px; max-height: 320px; overflow-y: auto;">
            <a-tree
              v-if="deptTreeData.length"
              v-model:expanded-keys="expandedKeys"
              :checked-keys="treeCheckedKeys"
              :tree-data="deptTreeData"
              checkable
              :check-strictly="!linkParentChild"
              @check="handleTreeCheck"
            />
            <a-empty v-else description="暂无部门数据" />
          </div>
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>
