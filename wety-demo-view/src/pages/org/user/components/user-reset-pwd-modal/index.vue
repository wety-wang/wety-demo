<script lang="ts" setup>
import type { FormInstance } from 'ant-design-vue'
import type { OrgUserInfoVO } from '~@/api/org/user'
import { message } from 'ant-design-vue'
import { resetUserPassword } from '~@/api/org/user'

const visible = ref(false)
const confirmLoading = ref(false)
const formRef = ref<FormInstance>()
const targetUser = ref<OrgUserInfoVO | null>(null)

const formData = reactive({
  newPassword: '',
  confirmPassword: '',
})

function open(record: OrgUserInfoVO) {
  targetUser.value = record
  formData.newPassword = ''
  formData.confirmPassword = ''
  formRef.value?.clearValidate()
  visible.value = true
}

function validateConfirmPassword(_rule: any, value: string) {
  if (!value)
    return Promise.reject(new Error('请再次输入新密码'))
  if (value !== formData.newPassword)
    return Promise.reject(new Error('两次输入的密码不一致'))
  return Promise.resolve()
}

async function handleOk() {
  try {
    await formRef.value?.validate()
    if (!targetUser.value?.code)
      return
    confirmLoading.value = true
    await resetUserPassword({
      code: targetUser.value.code,
      newPassword: formData.newPassword,
    })
    message.success('密码重置成功')
    visible.value = false
  }
  catch {
    // 校验或接口异常阻断关闭
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
    :title="`重置密码 - ${targetUser?.userName || ''}`"
    :confirm-loading="confirmLoading"
    width="460px"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ style: { width: '90px' } }"
      :wrapper-col="{ span: 24 }"
    >
      <a-form-item
        name="newPassword"
        label="新密码"
        :rules="[
          { required: true, message: '请输入新密码' },
          { min: 6, message: '密码长度不能少于 6 位' },
        ]"
      >
        <a-input-password v-model:value="formData.newPassword" :maxlength="50" placeholder="请输入新密码（至少6位）" />
      </a-form-item>

      <a-form-item
        name="confirmPassword"
        label="确认密码"
        :rules="[{ required: true, validator: validateConfirmPassword }]"
      >
        <a-input-password v-model:value="formData.confirmPassword" :maxlength="50" placeholder="请再次输入新密码" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
