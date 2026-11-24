<script lang="ts" setup>
import type { FormInstance } from 'ant-design-vue'
import type { AccountInfoVO, UserAddDTO, UserUpdateDTO } from '~@/api/user-manage/user'
import { addUser, updateUser } from '~@/api/user-manage/user'

// 事件：父组件监听确定/取消
const emit = defineEmits(['ok', 'cancel'])

// 用户类型枚举映射
const UserTypeMap: Record<any, { label: string, value: string }> = {
  'admin': { label: '管理员', value: 'admin' },
  'normal': { label: '普通用户', value: 'normal' },
}

// 弹窗显隐与修改/新增态
const visible = ref(false)
const isUpdate = ref(false)

// 表单引用与数据模型
const formRef = ref<FormInstance>()
const formData = reactive<{ code?: string, userName: string, loginName: string, password?: string, email?: string, phoneNumber?: string, type?: string }>({
  userName: '',
  loginName: '',
  password: '',
  email: '',
  phoneNumber: '',
  type: '',
})

// 计算标题
const title = computed(() => isUpdate.value ? '修改用户' : '新增用户')

// 对外暴露打开方法
function open(record?: AccountInfoVO) {
  visible.value = true
  isUpdate.value = !!record?.code
  // 修改场景下填充数据；新增场景清空
  if (record) {
    formData.code = record.code
    formData.userName = record.userName
    formData.loginName = record.loginName
    formData.email = record.email
    formData.phoneNumber = record.phoneNumber
    formData.password = '' // 修改不展示/不提交密码
    formData.type = record.type
  }
  else {
    formData.code = undefined
    formData.userName = ''
    formData.loginName = ''
    formData.email = ''
    formData.phoneNumber = ''
    formData.password = ''
    formData.type = 'normal'
  }
}

// 提交处理：根据 isAdmin 与 isUpdate 分流调用后端接口
async function handleOk() {
  try {
    await formRef.value?.validate()
    if (isUpdate.value) {
      // 修改只允许修改姓名/邮箱/手机号
      const payload: UserUpdateDTO = {
        code: formData.code!,
        userName: formData.userName,
        email: formData.email,
        phoneNumber: formData.phoneNumber,
      }
      await updateUser(payload)
    }
    else {
      // 新增需要密码与登录名
      const payload: UserAddDTO = {
        userName: formData.userName,
        loginName: formData.loginName,
        password: formData.password!,
        email: formData.email,
        phoneNumber: formData.phoneNumber,
        type: formData.type,
      }
      await addUser(payload)
    }
    emit('ok')
    visible.value = false
  }
  catch (e) {
    // 表单校验失败或接口异常直接阻断关闭
  }
}

function handleCancel() {
  formRef.value?.resetFields()
  emit('cancel')
}

// 暴露方法给父组件调用
defineExpose({ open })
</script>

<template>
  <a-modal v-model:open="visible" :title="title" @ok="handleOk" @cancel="handleCancel">
    <!-- 登录名在修改模式下不可修改；密码仅新增时填写 -->
    <a-form ref="formRef" :model="formData" :label-col="{ style: { width: '100px' } }" :wrapper-col="{ span: 24 }">
      <a-form-item name="userName" label="用户名" :rules="[{ required: true, message: '请输入用户名' }]">
        <a-input v-model:value="formData.userName" :maxlength="50" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item name="loginName" label="登录名" :rules="isUpdate ? [] : [{ required: true, message: '请输入登录名' }]">
        <a-input v-model:value="formData.loginName" :disabled="isUpdate" :maxlength="50" placeholder="请输入登录名" />
      </a-form-item>
      <a-form-item v-if="!isUpdate" name="password" label="密码" :rules="[{ required: true, message: '请输入密码' }]">
        <a-input-password v-model:value="formData.password" :maxlength="50" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item name="email" label="邮箱">
        <a-input v-model:value="formData.email" :maxlength="100" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item name="phoneNumber" label="手机号">
        <a-input v-model:value="formData.phoneNumber" :maxlength="20" placeholder="请输入手机号" />
      </a-form-item>
      <a-form-item name="type" label="用户类型" :rules="[{ required: true, message: '请选择用户类型' }]">
        <a-radio-group v-model:value="formData.type" :options="Object.values(UserTypeMap)" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
