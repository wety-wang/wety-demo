<script lang="ts" setup>
import type { FormInstance } from 'ant-design-vue'
import type { CodeNameDTO } from '~@/api/constant'
import type { DeptTreeVO } from '~@/api/org/dept'
import type { OrgUserAddDTO, OrgUserInfoVO, OrgUserUpdateDTO } from '~@/api/org/user'
import { message } from 'ant-design-vue'
import { queryDeptTree } from '~@/api/org/dept'
import { listAllPosition } from '~@/api/org/position'
import { listAllRole } from '~@/api/org/role'
import { addUser, updateUser } from '~@/api/org/user'

const emit = defineEmits<(e: 'ok') => void>()

interface OpenOptions {
  record?: OrgUserInfoVO
  defaultDeptCode?: string
}

const visible = ref(false)
const confirmLoading = ref(false)
const isUpdate = ref(false)
const isTargetSuperAdmin = ref(false)
const formRef = ref<FormInstance>()

// 下拉选项
const deptTreeRaw = ref<DeptTreeVO[]>([])
const positionOptions = ref<Array<{ label: string, value: string }>>([])
const roleOptions = ref<Array<{ label: string, value: string }>>([])

const formData = reactive<{
  code?: string
  userName: string
  loginName: string
  password: string
  email?: string
  phoneNumber?: string
  primaryDeptCode: string
  positionCodes: string[]
  roleCodes: string[]
  disabled: number
}>({
  code: undefined,
  userName: '',
  loginName: '',
  password: '',
  email: '',
  phoneNumber: '',
  primaryDeptCode: '',
  positionCodes: [],
  roleCodes: [],
  disabled: 0,
})

const title = computed(() => (isUpdate.value ? '修改用户' : '新增用户'))

const deptTreeSelectData = computed(() => {
  function mapNode(nodes: DeptTreeVO[]): any[] {
    return (nodes || []).map(node => ({
      title: node.name,
      value: node.code,
      key: node.code,
      disabled: node.status === 0,
      children: node.children?.length ? mapNode(node.children) : undefined,
    }))
  }
  return mapNode(deptTreeRaw.value)
})

async function loadOptions() {
  const [treeRes, posRes, roleRes] = await Promise.all([
    queryDeptTree({}).catch(() => [] as DeptTreeVO[]),
    listAllPosition().catch(() => [] as CodeNameDTO[]),
    listAllRole().catch(() => [] as CodeNameDTO[]),
  ])
  deptTreeRaw.value = treeRes || []
  positionOptions.value = (posRes || []).map(item => ({ label: item.name, value: item.code }))
  roleOptions.value = (roleRes || []).map(item => ({ label: item.name, value: item.code }))
}

async function open(options: OpenOptions = {}) {
  visible.value = true
  formRef.value?.clearValidate()

  await loadOptions()

  if (options.record?.code) {
    const record = options.record
    isUpdate.value = true
    isTargetSuperAdmin.value = record.superAdmin === 1
    formData.code = record.code
    formData.userName = record.userName
    formData.loginName = record.loginName
    formData.password = ''
    formData.email = record.email || ''
    formData.phoneNumber = record.phoneNumber || ''
    formData.primaryDeptCode = record.primaryDeptCode || ''
    formData.positionCodes = [...(record.positionCodes || [])]
    formData.roleCodes = [...(record.roleCodes || [])]
    formData.disabled = record.disabled ?? 0
  }
  else {
    isUpdate.value = false
    isTargetSuperAdmin.value = false
    formData.code = undefined
    formData.userName = ''
    formData.loginName = ''
    formData.password = ''
    formData.email = ''
    formData.phoneNumber = ''
    formData.primaryDeptCode = options.defaultDeptCode || deptTreeRaw.value[0]?.code || ''
    formData.positionCodes = []
    formData.roleCodes = []
    formData.disabled = 0
  }
}

async function handleOk() {
  try {
    await formRef.value?.validate()
    confirmLoading.value = true
    if (isUpdate.value && formData.code) {
      const payload: OrgUserUpdateDTO = {
        code: formData.code,
        userName: formData.userName.trim(),
        email: formData.email?.trim() || undefined,
        phoneNumber: formData.phoneNumber?.trim() || undefined,
        primaryDeptCode: formData.primaryDeptCode,
        positionCodes: formData.positionCodes,
        roleCodes: formData.roleCodes,
      }
      await updateUser(payload)
      message.success('用户修改成功')
    }
    else {
      const payload: OrgUserAddDTO = {
        loginName: formData.loginName.trim(),
        userName: formData.userName.trim(),
        password: formData.password,
        email: formData.email?.trim() || undefined,
        phoneNumber: formData.phoneNumber?.trim() || undefined,
        primaryDeptCode: formData.primaryDeptCode,
        positionCodes: formData.positionCodes,
        roleCodes: formData.roleCodes,
        disabled: formData.disabled,
      }
      await addUser(payload)
      message.success('用户新增成功')
    }
    visible.value = false
    emit('ok')
  }
  catch {
    // 表单校验或接口异常阻断关闭
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
    width="600px"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <a-alert
      v-if="isTargetSuperAdmin"
      type="info"
      show-icon
      message="当前编辑账号为内置超级管理员，默认拥有全部功能与数据权限。"
      style="margin-bottom: 16px;"
    />

    <a-form
      ref="formRef"
      :model="formData"
      :label-col="{ style: { width: '90px' } }"
      :wrapper-col="{ span: 24 }"
    >
      <a-form-item
        name="userName"
        label="姓名"
        :rules="[{ required: true, message: '请输入姓名' }]"
      >
        <a-input v-model:value="formData.userName" :maxlength="50" placeholder="请输入姓名" />
      </a-form-item>

      <a-form-item
        name="loginName"
        label="登录名"
        :rules="isUpdate ? [] : [{ required: true, message: '请输入登录名' }]"
      >
        <a-input
          v-model:value="formData.loginName"
          :disabled="isUpdate"
          :maxlength="50"
          placeholder="请输入登录名"
        />
      </a-form-item>

      <a-form-item
        v-if="!isUpdate"
        name="password"
        label="初始密码"
        :rules="[
          { required: true, message: '请输入初始密码' },
          { min: 6, message: '密码长度不能少于 6 位' },
        ]"
      >
        <a-input-password v-model:value="formData.password" :maxlength="50" placeholder="请输入初始密码（至少6位）" />
      </a-form-item>

      <a-form-item
        name="primaryDeptCode"
        label="主部门"
        :rules="[{ required: true, message: '请选择主部门' }]"
      >
        <a-tree-select
          v-model:value="formData.primaryDeptCode"
          :tree-data="deptTreeSelectData"
          tree-default-expand-all
          show-search
          tree-node-filter-prop="title"
          placeholder="请选择主部门"
          style="width: 100%;"
        />
      </a-form-item>

      <a-form-item name="positionCodes" label="岗位">
        <a-select
          v-model:value="formData.positionCodes"
          mode="multiple"
          :options="positionOptions"
          allow-clear
          show-search
          option-filter-prop="label"
          placeholder="请选择岗位（可多选）"
        />
      </a-form-item>

      <a-form-item name="roleCodes" label="角色">
        <a-select
          v-model:value="formData.roleCodes"
          mode="multiple"
          :options="roleOptions"
          allow-clear
          show-search
          option-filter-prop="label"
          placeholder="请选择角色（可多选）"
        />
      </a-form-item>

      <a-form-item name="phoneNumber" label="手机号">
        <a-input v-model:value="formData.phoneNumber" :maxlength="20" placeholder="请输入手机号" />
      </a-form-item>

      <a-form-item name="email" label="邮箱">
        <a-input v-model:value="formData.email" :maxlength="100" placeholder="请输入邮箱" />
      </a-form-item>

      <a-form-item v-if="!isUpdate" name="disabled" label="状态">
        <a-radio-group v-model:value="formData.disabled">
          <a-radio :value="0">
            启用
          </a-radio>
          <a-radio :value="1">
            禁用
          </a-radio>
        </a-radio-group>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
