<script setup lang="ts">
import type { FormInstance } from 'ant-design-vue'

defineOptions({
  name: 'BasicForm',
})

const formRef = ref<FormInstance>()
async function handleSubmit() {
  try {
    const values = await formRef.value?.validateFields()
    console.log('Success:', values)
  }
  catch (errorInfo) {
    console.log('Failed:', errorInfo)
  }
}
const formState = reactive<Record<string, any>>({
  name: null,
  buildTime: [],
  goal: '',
  standard: '',
  client: '',
  invites: '',
  weight: 0,
  target: 1,
})
</script>

<template>
  <page-container>
    <a-card :body-style="{ padding: '24px 32px' }" :bordered="false">
      <a-form ref="formRef" :model="formState">
        <a-form-item
          name="name"
          label="标题"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :rules="[{ required: true, message: '请输入标题' }]"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
        >
          <a-input
            v-model:value="formState.name"
            placeholder="请输入标题"
          />
        </a-form-item>
        <a-form-item
          label="起止日期"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :rules="[{ required: true, message: '请选择起止日期' }]"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          name="buildTime"
        >
          <a-range-picker
            v-model:value="formState.buildTime"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item
          label="目标描述"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :rules="[{ required: true, message: '请输入目标描述' }]"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          name="goal"
        >
          <a-textarea
            v-model:value="formState.goal"
            :rows="4"
            placeholder="请输入你的阶段性工作目标"
          />
        </a-form-item>
        <a-form-item
          label="衡量标准"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :rules="[{ required: true, message: '请输入衡量标准' }]"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          name="standard"
        >
          <a-textarea
            v-model:value="formState.standard"
            :rows="4"
            placeholder="请输入衡量标准"
          />
        </a-form-item>
        <a-form-item
          label="客户"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :rules="[{ required: true, message: '请输入客户' }]"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          name="client"
        >
          <a-input
            v-model:value="formState.client"
            name="client"
            placeholder="请描述你的服务对象，内部也可以"
          />
        </a-form-item>
        <a-form-item
          label="邀评人"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          :required="false"
          name="invites"
        >
          <a-input v-model:value="formState.invites" name="invites" placeholder="请联系被邀请人做评审" />
        </a-form-item>
        <a-form-item
          label="权重"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          :required="false"
          name="weight"
        >
          <a-input-number v-model:value="formState.weight" :min="0" :max="100" />
          <span> %</span>
        </a-form-item>
        <a-form-item
          label="公开设置"
          :label-col="{ lg: { span: 7 }, sm: { span: 7 } }"
          :wrapper-col="{ lg: { span: 10 }, sm: { span: 17 } }"
          :required="false"
          help="客户、邀评人默认被包含进公开范围"
          name="target"
        >
          <a-radio-group v-model:value="formState.target">
            <a-radio :value="1">
              公开
            </a-radio>
            <a-radio :value="2">
              部分公开
            </a-radio>
            <a-radio :value="3">
              不公开
            </a-radio>
          </a-radio-group>
          <a-form-item v-show="formState.target === 2">
            <a-select mode="multiple">
              <a-select-option value="4">
                同事A
              </a-select-option>
              <a-select-option value="5">
                同事B
              </a-select-option>
              <a-select-option value="6">
                同事C
              </a-select-option>
            </a-select>
          </a-form-item>
        </a-form-item>
        <a-form-item
          :wrapper-col="{ span: 24 }"
          style="text-align: center"
        >
          <a-button type="primary" @click="handleSubmit">
            提交
          </a-button>
          <a-button style="margin-left: 8px">
            保存
          </a-button>
        </a-form-item>
      </a-form>
    </a-card>
  </page-container>
</template>
