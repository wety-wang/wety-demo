<script setup lang="ts">
import { CloseCircleOutlined, RightOutlined } from '@ant-design/icons-vue'

const result = computed(() => {
  return {
    title: '提交失败',
    description: '请核对并修改以下信息后，再重新提交。',
  }
})
</script>

<template>
  <a-card :bordered="false">
    <a-result status="error" :title="result.title" :sub-title="result.description">
      <template #extra>
        <a-button type="primary">
          返回修改
        </a-button>
      </template>
      <div class="desc flex flex-col gap-2">
        <div class="font-500 ml-4 text-4">
          您提交的内容有如下错误：
        </div>
        <div class="ml-4">
          <CloseCircleOutlined class="text-red-6" />
          您的账户已被冻结
          <a class="ml-4 c-primary" hover="c-primary-hover">立即解冻 <RightOutlined /></a>
        </div>
        <div class="ml-4">
          <CloseCircleOutlined class="text-red-6" />
          您的账户还不具备申请资格
          <a class="ml-4 c-primary" hover="c-primary-hover">立即升级 <RightOutlined /></a>
        </div>
      </div>
    </a-result>
  </a-card>
</template>
