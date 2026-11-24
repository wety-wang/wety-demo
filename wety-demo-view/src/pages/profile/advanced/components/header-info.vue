<script setup lang="ts">
const command = ref('a')
const activeKey = ref()
</script>

<template>
  <a-row>
    <a-col :span="19">
      <span class="text-20px font-medium">
        订单号: 335231129436
      </span>
    </a-col>
    <a-col :span="5">
      <a-radio-group v-model:value="command" button-style="solid">
        <a-radio-button value="a">
          操作一
        </a-radio-button>
        <a-radio-button value="b">
          操作二
        </a-radio-button>
        <a-radio-button value="c">
          操作三
        </a-radio-button>
      </a-radio-group>
    </a-col>
  </a-row>
  <a-card :bordered="false" class="my-5">
    <a-row>
      <a-col :span="8">
        <div>
          <p>
            创建人: windlil
          </p>
          <p>
            创建时间: 2020-12-12
          </p>
          <p>
            生效日期: 2021-01-01
          </p>
        </div>
      </a-col>
      <a-col :span="8">
        <div>
          <p>
            产品: XX服务
          </p>
          <p>
            产品单据: 12345
          </p>
          <p>
            备注信息: 请于两个工作日内确认
          </p>
        </div>
      </a-col>
      <a-col :span="8" class="pl-45">
        <div class="flex">
          <div class="flex flex-col items-end mr-5">
            <p class="text-gray-400">
              状态
            </p>
            <p class="text-24px">
              已完成
            </p>
          </div>
          <div class="flex flex-col items-end">
            <p class="text-gray-400">
              金额
            </p>
            <p class="text-24px">
              ￥666.66
            </p>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-card>
  <a-tabs v-model:active-key="activeKey" class="mb--7">
    <a-tab-pane key="1" tab="详细" />
    <a-tab-pane key="2" tab="规则" force-render />
  </a-tabs>
</template>

<style scoped lang="less">
:deep(.ant-btn) {
  border-radius: 0 !important;
}
:deep(.ant-card-body) {
  padding: 0;
}
</style>
