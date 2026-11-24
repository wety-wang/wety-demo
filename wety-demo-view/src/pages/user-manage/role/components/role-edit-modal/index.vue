<script setup lang="ts">
import type { FormInstance } from 'ant-design-vue'
import type { RoleAddDTO, RoleInfoVO, RoleUpdateDTO } from '~@/api/user-manage/role'
import { message } from 'ant-design-vue'
import { addRole, updateRole } from '~@/api/user-manage/role'

const emit = defineEmits<(e: 'ok') => void>()
// 弹窗显隐控制
const visible = ref(false)
// 是否是更新模式（有code即表示更新）
const isUpdate = ref(false)
// 表单引用
const formRef = ref<FormInstance>()
// 表单数据
const formData = reactive<{ code?: string, name: string }>({
  code: undefined,
  name: '',
})

// 打开弹窗（传入记录则为修改模式，否则为新增）
function open(record?: RoleInfoVO) {
  if (record) {
    isUpdate.value = true
    formData.code = record.code
    formData.name = record.name
  }
  else {
    isUpdate.value = false
    formData.code = undefined
    formData.name = ''
  }
  visible.value = true
}

// 关闭弹窗并重置数据
function close() {
  visible.value = false
}

// 校验规则
const rules = {
  name: [{ required: true, message: '请输入角色名称' }],
}

// 提交处理：根据模式调用新增或更新接口
async function handleOk() {
  try {
    await formRef.value?.validate()
    if (isUpdate.value && formData.code) {
      const payload: RoleUpdateDTO = { code: formData.code, name: formData.name }
      await updateRole(payload)
      message.success('角色更新成功')
    }
    else {
      const payload: RoleAddDTO = { name: formData.name }
      await addRole(payload)
      message.success('角色新增成功')
    }
    // 关闭并触发刷新
    close()
    emit('ok')
  }
  catch (e) {
    // 表单校验或接口异常
  }
}

// 暴露open方法供外部调用
defineExpose({ open })
</script>

<template>
  <a-modal v-model:open="visible" :title="isUpdate ? '修改角色' : '新增角色'" @ok="handleOk" @cancel="close">
    <a-form ref="formRef" :model="formData" :rules="rules">
      <a-form-item label="角色名称" name="name">
        <!-- 角色名称输入框 -->
        <a-input v-model:value="formData.name" placeholder="请输入角色名称" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
