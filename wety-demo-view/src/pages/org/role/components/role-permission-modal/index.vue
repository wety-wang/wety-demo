<script setup lang="ts">
import type { PermissionHolder } from '~@/api/org/role'
import { message } from 'ant-design-vue'
import { queryRolePermission, updateRolePermission } from '~@/api/org/role'

const emit = defineEmits<(e: 'ok') => void>()

const visible = ref(false)
const loading = ref(false)
const confirmLoading = ref(false)
const roleCode = ref<string>('')
const treeData = ref<any[]>([])
const checkedKeys = ref<string[]>([])

function transformToTree(list: PermissionHolder[]): any[] {
  return (list || []).map(item => ({
    key: item.code,
    title: item.name,
    children: item.children?.length ? transformToTree(item.children) : undefined,
  }))
}

async function open(code: string) {
  roleCode.value = code
  visible.value = true
  loading.value = true
  try {
    const data = await queryRolePermission({ code })
    treeData.value = transformToTree(data?.hierarchicalList || [])
    checkedKeys.value = (data?.codes || []) as string[]
  }
  finally {
    loading.value = false
  }
}

function close() {
  visible.value = false
}

async function handleOk() {
  confirmLoading.value = true
  try {
    await updateRolePermission({
      roleCode: roleCode.value,
      permissionCodeList: checkedKeys.value,
    })
    message.success('菜单权限修改成功')
    close()
    emit('ok')
  }
  catch {
    // ignore
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
    title="菜单功能权限"
    :confirm-loading="confirmLoading"
    width="720px"
    @ok="handleOk"
    @cancel="close"
  >
    <a-spin :spinning="loading">
      <div style="max-height: 460px; overflow-y: auto; padding: 4px 0;">
        <a-tree
          v-if="treeData.length"
          v-model:checked-keys="checkedKeys"
          :tree-data="treeData"
          checkable
          :default-expand-all="true"
        />
        <a-empty v-else description="暂无权限树数据" />
      </div>
    </a-spin>
  </a-modal>
</template>
