<script setup lang="ts">
import type { PermissionHolder } from '~@/api/user-manage/role'
import { message } from 'ant-design-vue'
import { queryRolePermission, updateRolePermission } from '~@/api/user-manage/role'

const emit = defineEmits<(e: 'ok') => void>()
// 弹窗状态
const visible = ref(false)
const loading = ref(false)
// 当前角色编码
const roleCode = ref<string>('')
// 树数据与选中项
const treeData = ref<any[]>([])
const checkedKeys = ref<string[]>([])

// 将后端权限结构转换为Ant Tree数据结构
function transformToTree(list: PermissionHolder[]): any[] {
  return (list || [])
    .map(item => ({
      key: item.code,
      title: item.name,
      children: item.children?.length ? transformToTree(item.children) : undefined,
    }))
}

// 打开弹窗并加载权限数据
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

// 保存权限变更
async function handleOk() {
  try {
    await updateRolePermission({ roleCode: roleCode.value, permissionCodeList: checkedKeys.value })
    message.success('角色权限修改成功')
    close()
    emit('ok')
  }
  catch (e) {
    // ignore
  }
}

defineExpose({ open })
</script>

<template>
  <a-modal v-model:open="visible" title="角色权限" :confirm-loading="loading" width="720px" @ok="handleOk" @cancel="close">
    <!-- 权限树：只允许勾选叶子节点 -->
    <a-tree
      v-model:checked-keys="checkedKeys"
      :tree-data="treeData"
      checkable
      :default-expand-all="true"
    />
  </a-modal>
</template>
