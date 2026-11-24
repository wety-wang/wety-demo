<script setup lang="ts">
import type { FormInstance } from 'ant-design-vue'
import type { RoleAddDTO, RoleInfoVO, RoleUpdateDTO } from '~@/api/org/role'
import { message } from 'ant-design-vue'
import { addRole, DATA_SCOPE_OPTIONS, updateRole } from '~@/api/org/role'

const emit = defineEmits<(e: 'ok') => void>()

const visible = ref(false)
const confirmLoading = ref(false)
const isUpdate = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<{ code?: string, name: string, dataScope: number }>({
  code: undefined,
  name: '',
  dataScope: 5,
})

function open(record?: RoleInfoVO) {
  formRef.value?.clearValidate()
  if (record?.code) {
    isUpdate.value = true
    formData.code = record.code
    formData.name = record.name
    formData.dataScope = record.dataScope ?? 5
  }
  else {
    isUpdate.value = false
    formData.code = undefined
    formData.name = ''
    formData.dataScope = 5
  }
  visible.value = true
}

function close() {
  visible.value = false
}

const rules = {
  name: [{ required: true, message: '请输入角色名称' }],
}

async function handleOk() {
  try {
    await formRef.value?.validate()
    confirmLoading.value = true
    if (isUpdate.value && formData.code) {
      const payload: RoleUpdateDTO = {
        code: formData.code,
        name: formData.name.trim(),
      }
      await updateRole(payload)
      message.success('角色更新成功')
    }
    else {
      const payload: RoleAddDTO = {
        name: formData.name.trim(),
        dataScope: formData.dataScope,
      }
      await addRole(payload)
      message.success('角色新增成功')
    }
    close()
    emit('ok')
  }
  catch {
    // 表单校验或接口异常阻断关闭
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
    :title="isUpdate ? '修改角色' : '新增角色'"
    :confirm-loading="confirmLoading"
    width="500px"
    @ok="handleOk"
    @cancel="close"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-col="{ style: { width: '96px' } }"
      :wrapper-col="{ span: 24 }"
    >
      <a-form-item label="角色名称" name="name">
        <a-input v-model:value="formData.name" :maxlength="50" placeholder="请输入角色名称" />
      </a-form-item>

      <a-form-item v-if="!isUpdate" label="数据权限范围" name="dataScope">
        <a-select
          v-model:value="formData.dataScope"
          :options="DATA_SCOPE_OPTIONS.map(item => ({ label: item.label, value: item.value }))"
          placeholder="请选择初始数据权限范围"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
