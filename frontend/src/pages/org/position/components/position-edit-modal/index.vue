<script lang="ts" setup>
import type { FormInstance } from 'ant-design-vue'
import type { PositionAddDTO, PositionInfoVO, PositionUpdateDTO } from '~@/api/org/position'
import { message } from 'ant-design-vue'
import { addPosition, updatePosition } from '~@/api/org/position'

const emit = defineEmits<(e: 'ok') => void>()

const visible = ref(false)
const confirmLoading = ref(false)
const isUpdate = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<{
  code?: string
  postCode: string
  name: string
  sortOrder: number
  status: number
  remark?: string
}>({
  code: undefined,
  postCode: '',
  name: '',
  sortOrder: 0,
  status: 1,
  remark: '',
})

const title = computed(() => (isUpdate.value ? '修改岗位' : '新增岗位'))

function open(record?: PositionInfoVO) {
  visible.value = true
  formRef.value?.clearValidate()
  if (record?.code) {
    isUpdate.value = true
    formData.code = record.code
    formData.postCode = record.postCode
    formData.name = record.name
    formData.sortOrder = record.sortOrder ?? 0
    formData.status = record.status ?? 1
    formData.remark = record.remark || ''
  }
  else {
    isUpdate.value = false
    formData.code = undefined
    formData.postCode = ''
    formData.name = ''
    formData.sortOrder = 0
    formData.status = 1
    formData.remark = ''
  }
}

async function handleOk() {
  try {
    await formRef.value?.validate()
    confirmLoading.value = true
    if (isUpdate.value && formData.code) {
      const payload: PositionUpdateDTO = {
        code: formData.code,
        postCode: formData.postCode.trim(),
        name: formData.name.trim(),
        sortOrder: formData.sortOrder,
        status: formData.status,
        remark: formData.remark?.trim() || undefined,
      }
      await updatePosition(payload)
      message.success('岗位修改成功')
    }
    else {
      const payload: PositionAddDTO = {
        postCode: formData.postCode.trim(),
        name: formData.name.trim(),
        sortOrder: formData.sortOrder,
        status: formData.status,
        remark: formData.remark?.trim() || undefined,
      }
      await addPosition(payload)
      message.success('岗位新增成功')
    }
    visible.value = false
    emit('ok')
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
    :title="title"
    :confirm-loading="confirmLoading"
    width="520px"
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
        name="name"
        label="岗位名称"
        :rules="[{ required: true, message: '请输入岗位名称' }]"
      >
        <a-input v-model:value="formData.name" :maxlength="100" placeholder="请输入岗位名称" />
      </a-form-item>

      <a-form-item
        name="postCode"
        label="岗位编码"
        :rules="[{ required: true, message: '请输入岗位编码' }]"
      >
        <a-input v-model:value="formData.postCode" :maxlength="64" placeholder="如：DEV、PM、HR" />
      </a-form-item>

      <a-form-item name="sortOrder" label="显示排序">
        <a-input-number v-model:value="formData.sortOrder" :min="0" :max="99999" style="width: 100%;" />
      </a-form-item>

      <a-form-item name="status" label="状态">
        <a-radio-group v-model:value="formData.status">
          <a-radio :value="1">
            启用
          </a-radio>
          <a-radio :value="0">
            停用
          </a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item name="remark" label="备注">
        <a-textarea v-model:value="formData.remark" :maxlength="255" :rows="3" placeholder="请输入备注" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
