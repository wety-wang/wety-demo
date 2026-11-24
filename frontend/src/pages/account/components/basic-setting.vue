<script setup lang="ts">
import type { UnwrapRef } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'

interface FormState {
  eamil: string
  name: string
  phoneNumber: string
  region: string | undefined
  address: string
  desc: string
}

const formRef = ref()
const labelCol = { span: 0 }
const wrapperCol = { span: 13 }
const formState: UnwrapRef<FormState> = reactive({
  eamil: 'AntdvPro@abc.com',
  name: 'AntdvPro',
  region: undefined,
  desc: '',
  address: '',
  phoneNumber: '',
})
const rules: any = computed(() => {
  return {
    name: [
      // 校验：用户名必填
      { required: true, message: '请输入用户名称', trigger: 'change' },
    ],

    phoneNumber: [
      // 校验：联系电话必填
      { required: true, message: '请输入联系电话', trigger: 'change' },
    ],
    address: [
      // 校验：地址必填
      { required: true, message: '请输入地址', trigger: 'change' },
    ],
    region: [
      // 校验：地区必选
      { required: true, message: '请选择地区', trigger: 'change' },
    ],
    eamil: [
      // 校验：邮箱必填
      { required: true, message: '请输入邮箱', trigger: 'change' },
    ],
    desc: [
      // 校验：个人简介必填
      { required: true, message: '请输入个人简介', trigger: 'blur' },
    ],
  }
})

function onSubmit() {
  formRef.value
    .validate()
    .then(() => {
      console.log('values', formState, toRaw(formState))
    })
    .catch((error: any) => {
      console.log('error', error)
    })
}

function handleChange() {
  console.log('change')
}
</script>

<template>
  <!-- 标题：基本设置 -->
  <a-card title="基本设置" :bordered="false">
    <a-row>
      <a-col :span="12">
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          :label-col="labelCol"
          :wrapper-col="wrapperCol"
        >
          <!-- 表单项：电子邮箱 -->
          <a-form-item :label-col="{ span: 24 }" label="电子邮箱" name="eamil">
            <a-input v-model:value="formState.eamil" placeholder="请输入" style="width: 320px;" />
          </a-form-item>
          <!-- 表单项：用户名称 -->
          <a-form-item :label-col="{ span: 24 }" label="用户名称" name="name">
            <a-input v-model:value="formState.name" placeholder="请输入" style="width: 320px;" />
          </a-form-item>
          <!-- 表单项：所在地区 -->
          <a-form-item label="所在地区" :label-col="{ span: 24 }" name="region">
            <a-select v-model:value="formState.region" placeholder="请选择">
              <a-select-option value="China">
                中国
              </a-select-option>
            </a-select>
          </a-form-item>
          <!-- 表单项：详细地址 -->
          <a-form-item :label-col="{ span: 24 }" label="详细地址" name="address">
            <a-input v-model:value="formState.address" placeholder="请输入" style="width: 320px;" />
          </a-form-item>
          <!-- 表单项：联系电话 -->
          <a-form-item :label-col="{ span: 24 }" label="联系电话" name="phoneNumber">
            <a-input v-model:value="formState.phoneNumber" placeholder="请输入" />
          </a-form-item>
          <!-- 表单项：个人简介 -->
          <a-form-item name="desc" label="个人简介" :label-col="{ span: 24 }">
            <a-textarea v-model:value="formState.desc" placeholder="请输入" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="onSubmit">
              提交
            </a-button>
          </a-form-item>
        </a-form>
      </a-col>
      <a-col :span="4">
        <!-- 头像区域标题 -->
        <p>头像</p>
        <div class="flex flex-col items-center">
          <a-avatar :size="100">
            <template #icon>
              <img src="https://gw.alipayobjects.com/zos/rmsportal/BiazfanxmamNRoxxVxka.png" alt="">
            </template>
          </a-avatar>
          <a-upload
            name="file"
            :file-list="[]"
            @change="handleChange"
          >
            <a-button class="mt-4">
              <UploadOutlined />
              更换头像
            </a-button>
          </a-upload>
        </div>
      </a-col>
    </a-row>
  </a-card>
</template>
