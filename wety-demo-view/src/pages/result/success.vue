<script setup lang="ts">
import { DingdingOutlined } from '@ant-design/icons-vue'

const { isMobile } = useQueryBreakpoints()

const result = computed(() => {
  return {
    title: '操作成功',
    description: '提交结果页用于反馈一系列操作任务的处理结果，如果仅是简单操作，使用 Message 全局提示反馈即可。',
  }
})
</script>

<template>
  <a-card :bordered="false">
    <a-result status="success" :title="result.title" :sub-title="result.description">
      <template #extra>
        <a-button type="primary">
          返回列表
        </a-button>
        <a-button class="ml-2">
          查看项目
        </a-button>
        <a-button class="ml-2">
          打印
        </a-button>
      </template>
      <div class="content">
        <div class="font-500 ml-4 text-4">
          项目名称：Kirk Lin 主动运营方案
        </div>
        <a-row class="ml-4">
          <a-col :xs="24" :sm="12" :md="12" :lg="12" :xl="6">
            <span>项目 ID：</span>
            20230824089
          </a-col>
          <a-col :xs="24" :sm="12" :md="12" :lg="12" :xl="6">
            <span>负责人：</span>
            Kirk Lin是谁？
          </a-col>
          <a-col :xs="24" :sm="24" :md="24" :lg="24" :xl="12">
            <span>生效时间：</span>
            2023-08-12 ~ 2024-08-12
          </a-col>
        </a-row>
        <a-steps :current="1" :direction="isMobile && 'horizontal' || 'horizontal'" progress-dot>
          <a-step title="创建项目">
            <span class="text-3">创建项目</span>
            <template #description>
              <div class="relative text-12px align-left left-42px">
                <div style="margin: 8px 0 4px">
                  Kirk Lin 创建项目 Kirk Lin 主动运营方案
                  <DingdingOutlined class="m-1 c-primary" />
                </div>
                <div>2023-08-17 12:32</div>
              </div>
            </template>
          </a-step>
          <a-step title="部门初审">
            <span class="text-3">部门初审</span>
            <template #description>
              <div class="relative text-12px align-left left-42px">
                <div style="margin: 8px 0 4px">
                  卡顿哥 香港主管未通过审核
                  <DingdingOutlined class="m-1 c-primary" />
                </div>
                <div>2023-08-17 13:32</div>
              </div>
            </template>
          </a-step>
          <a-step title="财务复核">
            <span class="text-3">财务复核</span>
          </a-step>
          <a-step title="完成">
            <span class="text-3">完成</span>
          </a-step>
        </a-steps>
      </div>
    </a-result>
  </a-card>
</template>
